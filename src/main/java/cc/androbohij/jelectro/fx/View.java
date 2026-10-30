package cc.androbohij.jelectro.fx;

import static cc.androbohij.jelectro.Settings.*;

import java.util.logging.Logger;

import cc.androbohij.jelectro.EField;
import cc.androbohij.jelectro.ListObserver;
import cc.androbohij.jelectro.objects.ChargedObject;
import cc.androbohij.jelectro.objects.Multipole;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
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
            switch (event.getCode()) {
                case SPACE:
                    eField.clearObjects();
                    break;
                case D:
                    Field2DOnly = !Field2DOnly;
                    drawField();
                    break;
                default:
                    break;
            }
        });

        scene.setFill(Color.BLACK);

        eField.subscribe(this);
        eField.createObject(new Multipole(Point3D.ZERO, 1e-7, 0.01, 4));

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
    
    private Group line(Point3D p1, Point3D p2, 
            boolean ignoreDepth, boolean vector) {
        Point3D directionVec = p2.subtract(p1);
        double length = directionVec.magnitude();

        double scale = vector ? Math.min(length, 1/vectorDensity) / length : 1.0;

        Point3D scaledVec = directionVec.multiply(scale);
        double scaledLength = scaledVec.magnitude();
        Point3D mid = p1.add(scaledVec.multiply(0.5));

        if (length == 0 || scaledLength < cullFieldVecs)
            return new Group();

        PhongMaterial mat = new PhongMaterial(Color.WHITE);
        mat.setSelfIlluminationMap(
            new Image(getClass().getResource("/selfIllum.png").toExternalForm()));

        // its secretly just really thin quads
        Line3D strip = new Line3D(0.0001f, (float)scaledLength, true);
        strip.setCullFace(CullFace.NONE);
        strip.setMaterial(mat);
        // plus wireframe mode so its always the same width onscreen
        strip.setDrawMode(DrawMode.LINE);
        if (ignoreDepth)
            strip.setDepthTest(DepthTest.DISABLE);

        Group line = new Group(strip);

        line.getTransforms().addAll(
            new Translate(mid.getX(), mid.getY(), mid.getZ()),
            new Rotate(-scaledVec.angle(Rotate.Y_AXIS),
                scaledVec.crossProduct(Rotate.Y_AXIS))
        );
        line.setMouseTransparent(true);
        return line;
    }

    private Group line(Point3D p1, Point3D p2, boolean ignoreDepth) {
        return line(p1, p2, ignoreDepth, false);
    }

    private void makeGrid() {
        root.getChildren().addAll(
            line(new Point3D(-5, 0, 0), new Point3D(5, 0, 0), true),
            line(new Point3D(0, 0, -5), new Point3D(0, 0, 5), true)
        );
    }

    private void drawField() {
        fieldVectors.getChildren().clear();
        if (eField.isEmpty())
            return;
        double xyMax = vectorBounds*vectorDensity;
        double zMax = Field2DOnly ? 0 : xyMax;
        for (double i = -xyMax; i <= xyMax; i++) {
            for (double j = -xyMax; j <= xyMax; j++) {
                for (double k = -zMax; k <= zMax; k++) {
                    double x = i / vectorDensity;
                    double y = k / vectorDensity;
                    double z = j / vectorDensity;
                    Point3D currentP = new Point3D(x, y, z);
                    Group line = line(
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
            drawField();
    }

    @Override
    public void listRemove(ChargedObject chargedObject) {
        objects.getChildren().remove(chargedObject.getShape());
        if (showField)
            drawField();
    }

    @Override
    public void listClear() {
        objects.getChildren().clear();
        if (showField)
            drawField();
    }

}