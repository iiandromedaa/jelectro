package cc.androbohij.jelectro.fx;

import static cc.androbohij.jelectro.Settings.*;

import java.util.logging.Logger;

import cc.androbohij.jelectro.EField;
import cc.androbohij.jelectro.ListObserver;
import cc.androbohij.jelectro.shapes.ChargedObject;
import cc.androbohij.jelectro.shapes.PointCharge;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class View extends Application implements ListObserver {

    private static final Logger log = Logger.getLogger(View.class.getName());
    private Group root;
    private Group objects;
    private Group fieldVectors;
    private Camera camera;

    private double mouseDownX;
    private double mouseDownY;

    private Rotate camPitch;
    private Rotate camYaw;

    private EField eField;

    public View() {
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            log.warning("3d is not supported");
            Platform.exit();
            return;
        }
        root = new Group();
        objects = new Group();
        fieldVectors = new Group();
        camera = new PerspectiveCamera(true);
        camPitch = new Rotate(0, Rotate.X_AXIS);
        camYaw = new Rotate(0, Rotate.Y_AXIS);
        eField = EField.getInstance();
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(root, 640, 480, true);

        Box mousePlane = new Box(10,0.01,10);
        mousePlane.setMaterial(new PhongMaterial(new Color(0, 0, 0, 0)));
        makeGrid();
        root.getChildren().addAll(mousePlane, objects, fieldVectors);

        Group cameraPivot = new Group(camera);
        camera.setTranslateZ(-10);
        camera.setFarClip(100);
        cameraPivot.getTransforms().addAll(camYaw, camPitch);
        
        scene.setCamera(camera);
        scene.setOnMousePressed(this::mousePressed);
        scene.setOnMouseDragged(this::mouseDrag);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                eField.clearObjects();
            }
        });
        scene.setFill(Color.BLACK);

        eField.subscribe(this);
        eField.createObject(new PointCharge(new Point3D(0.01, 0, 0), -1e-9));
        eField.createObject(new PointCharge(new Point3D(-0.01, 0, 0), 1e-9));

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    private void mousePressed(MouseEvent event) {
        if (event.getButton() != MouseButton.SECONDARY)
            return;
        mouseDownX = event.getSceneX();
        mouseDownY = event.getSceneY();
    }

    private void mouseDrag(MouseEvent event) {
        if (event.getButton() != MouseButton.SECONDARY)
            return;
        double dx = (event.getSceneX() - mouseDownX)*0.3;
        double dy = (event.getSceneY() - mouseDownY)*0.3;

        camPitch.setAngle(Math.max(-85, Math.min(camPitch.getAngle() - dy, 85)));
        camYaw.setAngle(camYaw.getAngle() + dx);

        mouseDownX = event.getSceneX();
        mouseDownY = event.getSceneY();
    }
    
    private Group straightPolyline(Point3D p1, Point3D p2, 
            boolean ignoreDepth, boolean scaleLines) {
        Point3D directionVec = p2.subtract(p1);
        double length = directionVec.magnitude();

        double scale = scaleLines ? Math.min(length, 1/vectorDensity) / length : 1.0;

        Point3D scaledVec = directionVec.multiply(scale);
        double scaledLength = scaledVec.magnitude();
        Point3D mid = p1.add(scaledVec.multiply(0.5));

        if (length == 0 || scaledLength < cullFieldVecs)
            return new Group();

        Cylinder cylinder = new Cylinder(0.005, scaledLength, 3);
        cylinder.setMaterial(new PhongMaterial(Color.WHITE));
        if (ignoreDepth)
            cylinder.setDepthTest(DepthTest.DISABLE);

        Group line = new Group(cylinder);

        line.getTransforms().addAll(
            new Translate(mid.getX(), mid.getY(), mid.getZ()),
            new Rotate(-scaledVec.angle(Rotate.Y_AXIS),
                scaledVec.crossProduct(Rotate.Y_AXIS))
        );
        line.setMouseTransparent(true);
        return line;
    }

    private Group straightPolyline(Point3D p1, Point3D p2, boolean ignoreDepth) {
        return straightPolyline(p1, p2, ignoreDepth, false);
    }

    private void makeGrid() {
        root.getChildren().addAll(
            straightPolyline(new Point3D(5, 0, 0), new Point3D(-5, 0, 0), true),
            straightPolyline(new Point3D(0, 0, 5), new Point3D(0, 0, -5), true)
        );
    }

    private void displayFieldVectors() {
        fieldVectors.getChildren().clear();
        if (eField.isEmpty())
            return;
        for (double i = -vectorBounds*vectorDensity; i < vectorBounds*vectorDensity; i++) {
            for (double j = -vectorBounds*vectorDensity; j < vectorBounds*vectorDensity; j++) {
                for (double k = -vectorBounds*vectorDensity; k <vectorBounds*vectorDensity; k++) {
                    double x = i / vectorDensity;
                    double y = (Field2DOnly) ? 0 : k / vectorDensity;
                    double z = j / vectorDensity;
                    Point3D currentP = new Point3D(x, y, z);
                    Group line = straightPolyline(
                        currentP,
                        eField.getEAtPoint(currentP).add(currentP), 
                        false,
                        true
                    );
                    if (!line.getChildren().isEmpty())
                        fieldVectors.getChildren().add(line);
                }
            }
        }
    }

    @Override
    public void listAdd(ChargedObject chargedObject) {
        objects.getChildren().add(chargedObject.getShape());
        if (showField)
            displayFieldVectors();
    }

    @Override
    public void listRemove(ChargedObject chargedObject) {
        objects.getChildren().remove(chargedObject.getShape());
        if (showField)
            displayFieldVectors();
    }

    @Override
    public void listClear() {
        objects.getChildren().clear();
        if (showField)
            displayFieldVectors();
    }

}