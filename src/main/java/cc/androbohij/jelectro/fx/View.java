package cc.androbohij.jelectro.fx;

import static cc.androbohij.jelectro.Settings.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import cc.androbohij.jelectro.EField;
import cc.androbohij.jelectro.ListObserver;
import cc.androbohij.jelectro.objects.ChargedObject;
import cc.androbohij.jelectro.objects.Multipole;
import cc.androbohij.jelectro.objects.PointCharge;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class View extends Application implements ListObserver {

    private static final Logger log = Logger.getLogger(View.class.getName());
    private Group root;
    private Group objects;
    private Group fieldVectors;
    private Group grid;
    private Group streamlines;
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
        grid = new Group();
        streamlines = new Group();
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
        drawGrid();
        root.getChildren().addAll(mousePlane, objects, fieldVectors, grid, streamlines);

        Group cameraPivot = new Group(camera);
        camera.setTranslateZ(-10);
        camera.setFarClip(100);
        cameraPivot.getTransforms().addAll(camYaw, camPitch);
        
        scene.setCamera(camera);
        scene.setOnMousePressed(this::mousePressed);
        scene.setOnMouseDragged(this::mouseDrag);

        // TODO: move this to its own method
        // TODO: or a static class holding the input handling methods?
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE:
                    eField.clearObjects();
                    break;
                case D:
                    field2DOnly = !field2DOnly;
                    drawField();
                    break;
                case S:
                    showStreamlines = !showStreamlines;
                    drawStreamlines();
                    break;
                case F:
                    showField = !showField;
                    drawField();
                    break;
                case G:
                    showGrid = !showGrid;
                    drawGrid();
                    break;
                case A:
                    drawArrowHeads = !drawArrowHeads;
                    drawField();
                    drawStreamlines();
                    break;
                default:
                    break;
            }
        });

        scene.setFill(Color.BLACK);

        eField.subscribe(this);

        // TODO: object creation UI, making objects in scene init can cause races
        eField.createObject(new PointCharge(new Point3D(0, 0, 0), 1e-10));
        eField.createObject(new Multipole(new Point3D(-0.5, 0, 1), 5e-9, 0.01, 2, 0, 15, 45, false));

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

    private void drawGrid() {
        grid.getChildren().clear();
        if (!showGrid)
            return;
        grid.getChildren().addAll(
            LineDrawer.draw(new Point3D(-5, 0, 0), new Point3D(5, 0, 0), false),
            LineDrawer.draw(new Point3D(0, 0, -5), new Point3D(0, 0, 5), false)
        );
    }

    private void drawField() {
        fieldVectors.getChildren().clear();
        if (eField.isEmpty() || !showField)
            return;
        double xyMax = vectorBounds*vectorDensity;
        double zMax = field2DOnly ? 0 : xyMax;
        for (double i = -xyMax; i <= xyMax; i++) {
            for (double j = -xyMax; j <= xyMax; j++) {
                for (double k = -zMax; k <= zMax; k++) {
                    double x = i / vectorDensity;
                    double y = k / vectorDensity;
                    double z = j / vectorDensity;
                    Point3D currentP = new Point3D(x, y, z);
                    Group line = LineDrawer.draw(
                        currentP,
                        eField.getEAtPoint(currentP).add(currentP),
                        true
                    );
                    if (!line.getChildren().isEmpty())
                        fieldVectors.getChildren().add(line);
                }
            }
        }
    }

    private void drawStreamlines() {
        long t = System.currentTimeMillis();
        streamlines.getChildren().clear();
        if (eField.isEmpty() || !showStreamlines)
            return;
        for (int p = 0; p < streamlinePasses; p++) {
            for (ChargedObject co : eField.getObjects()) {
                List<Point3D> points = fibonacciSphere(
                    co.getPosition(),
                    streamlineStartDistance*Math.max(p, 1), 
                    streamlinesPerObject, 2*p
                );
                System.out.println(points.size());
                List<Group> lines = points.parallelStream()
                    .distinct()
                    .map(point ->
                        LineDrawer.draw(eField.getStreamline(point, 
                            streamlineMaxSteps, streamlineStepSize, 
                            true))
                    ).collect(Collectors.toList());
                
                lines.addAll(points.parallelStream()
                    .distinct()
                    .map(point ->
                        LineDrawer.draw(eField.getStreamline(point, 
                            streamlineMaxSteps, streamlineStepSize, 
                            false))
                    ).collect(Collectors.toList())
                );
                Platform.runLater(() -> streamlines.getChildren().addAll(lines));
            }
        }
        System.out.println(System.currentTimeMillis()-t);
    }

    private List<Point3D> fibonacciSphere(Point3D center, double r, int n, double shift) {
        List<Point3D> points = new ArrayList<>();
        double goldenRatio = (1.0 + Math.sqrt(5.0)) / 2.0;
        double increment = Math.PI * 2.0 * (goldenRatio - 1.0) + shift;
        for (int i = 0; i < n; i++) {
            double inc = Math.acos(1 - (2*(i))/(double)n);
            double azi = increment*i;
            points.add(
                new Point3D(
                    Math.sin(inc) * Math.cos(azi), 
                    Math.cos(inc), 
                    Math.sin(inc) * Math.sin(azi)
                ).multiply(r)
                .add(center)
            );
        }
        return points;
    }

    @Override
    public void listAdd(ChargedObject chargedObject) {
        objects.getChildren().add(chargedObject.getHandle());
        drawField();
        drawStreamlines();
    }

    @Override
    public void listRemove(ChargedObject chargedObject) {
        objects.getChildren().remove(chargedObject.getHandle());
        drawField();
        drawStreamlines();
    }

    @Override
    public void listClear() {
        objects.getChildren().clear();
        drawField();
        drawStreamlines();
    }

}