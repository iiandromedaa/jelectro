package cc.androbohij.jelectro.fx;

import static cc.androbohij.jelectro.Settings.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cc.androbohij.jelectro.EField;
import cc.androbohij.jelectro.ListObserver;
import cc.androbohij.jelectro.fx.controllers.Sidebar;
import cc.androbohij.jelectro.objects.ChargedObject;
import cc.androbohij.jelectro.objects.Multipole;
import cc.androbohij.jelectro.objects.PointCharge;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class View extends Application implements ListObserver {

    private static final Logger log = Logger.getLogger(View.class.getName());
    private Group root, objects, fieldVectors, grid, streamlines, select, velocities;
    private Camera camera;

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
        select = new Group();
        velocities = new Group();

        camera = new PerspectiveCamera(true);
        eField = EField.getInstance();
    }

    @Override
    public void start(Stage stage) throws IOException {
        SubScene view3d = new SubScene(root, 1200, 800, true, SceneAntialiasing.DISABLED);

        root.getChildren().addAll(objects, fieldVectors, grid, streamlines, velocities, select);

        Group cameraPivot = new Group(camera);
        camera.setTranslateZ(-10);
        camera.setFarClip(100);
        cameraPivot.getTransforms().addAll(InputHandler.getCamYaw(), InputHandler.getCamPitch());
        
        view3d.setCamera(camera);
        view3d.setOnMousePressed(InputHandler::mousePressed);
        view3d.setOnMouseDragged(InputHandler::mouseDrag);

        view3d.setFill(Color.BLACK);

        eField.subscribe(this);

        // TODO: object creation UI, making objects in scene init can cause races
        eField.createObject(new PointCharge(new Point3D(0, 0, 0), 1e-10));
        eField.createObject(new Multipole(new Point3D(-0.5, 0, 1), 5e-9, 0.01, 2, 0, 15, 45, false));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml"));
        AnchorPane UI = loader.load();

        ((Sidebar)loader.getController()).setApp(this);

        UI.widthProperty().addListener((obs, oldVal, newVal) -> {
            view3d.setWidth(newVal.doubleValue());
        });

        UI.heightProperty().addListener((obs, oldVal, newVal) -> {
            view3d.setHeight(newVal.doubleValue());
        });

        UI.getChildren().add(0, view3d);

        Scene scene = new Scene(UI, 1200, 800);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        refresh();
    }

    public void refresh() {
        drawGrid();
        drawStreamlines();
        drawField();
        drawVelocities();
    }

    public void select(ChargedObject chargedObject) {
        select.getChildren().clear();
        if (!eField.getObjects().contains(chargedObject))
            return;
        Sphere marker = new Sphere(0.033);
        marker.setDepthTest(DepthTest.DISABLE);
        PhongMaterial mat = new PhongMaterial(Color.GOLD);
        mat.setSelfIlluminationMap(new Image(
            getClass().getResource("/textures/selfIllumYellow.png").toExternalForm()));
        marker.setMaterial(mat);
        marker.setTranslateX(chargedObject.getPositionX());
        marker.setTranslateY(chargedObject.getPositionY());
        marker.setTranslateZ(chargedObject.getPositionZ());
        select.getChildren().add(marker);
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

    private void drawVelocities() {
        velocities.getChildren().clear();
        if (eField.isEmpty() || !showVelocities)
            return;
        List<Group> lines = eField.getObjects().stream()
            .map(object ->
                LineDrawer.draw(object.getPosition(), 
                    object.getPosition().add(object.getVelocity()))
            ).filter(Objects::nonNull)
            .collect(Collectors.toList());
        velocities.getChildren().addAll(lines);
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
                List<Group> lines = points.stream()
                    .map(point ->
                        LineDrawer.draw(eField.getStreamline(point, 
                            streamlineMaxSteps, streamlineStepSize, 
                            true))
                    ).filter(Objects::nonNull)
                    .collect(Collectors.toList());
                
                lines.addAll(points.stream()
                    .map(point ->
                        LineDrawer.draw(eField.getStreamline(point, 
                            streamlineMaxSteps, streamlineStepSize, 
                            false))
                    ).filter(Objects::nonNull)
                    .collect(Collectors.toList())
                );
                Platform.runLater(() -> streamlines.getChildren().addAll(lines));
            }
        }
        log.info(String.format("Streamlines took %dms", System.currentTimeMillis()-t));
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
        refresh();
    }

    @Override
    public void listRemove(ChargedObject chargedObject) {
        objects.getChildren().remove(chargedObject.getHandle());
        refresh();
    }

    @Override
    public void listClear() {
        objects.getChildren().clear();
        refresh();
    }

    private static class InputHandler {

        private static double mouseDownX;
        private static double mouseDownY;

        private static Rotate camPitch = new Rotate(0, Rotate.X_AXIS);
        private static Rotate camYaw = new Rotate(0, Rotate.Y_AXIS);

        private static void mousePressed(MouseEvent event) {
            if (event.getButton() != MouseButton.SECONDARY)
                return;
            mouseDownX = event.getSceneX();
            mouseDownY = event.getSceneY();
        }

        private static void mouseDrag(MouseEvent event) {
            if (event.getButton() != MouseButton.SECONDARY)
                return;
            double dx = (event.getSceneX() - mouseDownX)*0.3;
            double dy = (event.getSceneY() - mouseDownY)*0.3;

            camPitch.setAngle(Math.max(-85, Math.min(camPitch.getAngle() - dy, 85)));
            camYaw.setAngle(camYaw.getAngle() + dx);

            mouseDownX = event.getSceneX();
            mouseDownY = event.getSceneY();
        }

        private static Rotate getCamPitch() {
            return camPitch;
        }

        private static Rotate getCamYaw() {
            return camYaw;
        }

    }

}