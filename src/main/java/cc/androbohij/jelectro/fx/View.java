package cc.androbohij.jelectro.fx;

import static cc.androbohij.jelectro.Settings.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cc.androbohij.jelectro.EField;
import cc.androbohij.jelectro.ListObserver;
import cc.androbohij.jelectro.fx.controllers.Sidebar;
import cc.androbohij.jelectro.objects.ChargedObject;
import cc.androbohij.jelectro.objects.LineCharge;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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
        Sphere marker = new Sphere(0.1);
        PhongMaterial mat = new PhongMaterial();
        LineDrawer.setUpMaterial(mat, Color.GOLD);
        marker.setDepthTest(DepthTest.DISABLE);
        marker.setMaterial(mat);
        marker.setTranslateX(chargedObject.getPositionX());
        marker.setTranslateY(chargedObject.getPositionY());
        marker.setTranslateZ(chargedObject.getPositionZ());
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(0.5), 
                new KeyValue(mat.diffuseColorProperty(), 
                    Color.TRANSPARENT, Interpolator.EASE_OUT))
        );
        select.getChildren().add(marker);
        timeline.play();
    }

    private void drawGrid() {
        grid.getChildren().clear();
        if (!showGrid)
            return;
        grid.getChildren().addAll(
            LineDrawer.draw(new Point3D(-5, 0, 0), new Point3D(5, 0, 0)),
            LineDrawer.draw(new Point3D(0, 0, -5), new Point3D(0, 0, 5))
        );
    }

    private void drawVelocities() {
        velocities.getChildren().clear();
        if (eField.isEmpty() || !showVelocities)
            return;
        List<Group> lines = eField.getObjects().stream()
            .map(object ->
                LineDrawer.draw(object.getPosition(), 
                    object.getPosition().add(object.getVelocity()),
                    LineDrawer.BLUE)
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
                        true,
                        LineDrawer.WHITE
                    );
                    if (line != null)
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
                List<Point3D> points;
                if (co instanceof LineCharge) {
                    points = pointsOnCylinder(
                        co.getPosition(), 
                        streamlineStartDistance*(p+1), 
                        streamlineResolution, p, 
                        (LineCharge) co
                    );
                } else
                    points = pointsOnSphere(
                        co.getPosition(),
                        streamlineStartDistance*(p+1), 
                        streamlineResolution, p, co
                    );
                List<Group> lines = new ArrayList<>();

                if (drawE)
                    collectLines(lines, points, eField::getEAtPoint, 
                        LineDrawer.WHITE, eScale);
                if (drawB)
                    collectLines(lines, points, eField::getBAtPoint, 
                        LineDrawer.ORANGE, bScale);
                
                streamlines.getChildren().addAll(lines);
            }
        }
        log.info(String.format("Streamlines took %dms", System.currentTimeMillis()-t));
    }

    private void collectLines(List<Group> lines, List<Point3D> points, 
        Function<Point3D, Point3D> function, PhongMaterial mat, double scale) {
        lines.addAll(points.stream()
            .map(point ->
                LineDrawer.drawWithOpacity(eField.getStreamline(point, 
                    streamlineMaxSteps, streamlineStepSize, 
                    true, function), mat, scale)
            ).filter(Objects::nonNull)
            .collect(Collectors.toList())
        );
        
        lines.addAll(points.stream()
            .map(point ->
                LineDrawer.drawWithOpacity(eField.getStreamline(point, 
                    streamlineMaxSteps, streamlineStepSize, 
                    false, function), mat, scale)
            ).filter(Objects::nonNull)
            .collect(Collectors.toList())
        );
    }

    private List<Point3D> pointsOnSphere(Point3D center, double r, 
        int n, int shift, ChargedObject co) {
        List<Point3D> points = new ArrayList<>();
        for (double i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double latitude = (Math.PI * i / (n - 1)) + (Math.PI*shift)/6;
                double longitude = (2 * Math.PI * j / n) + (Math.PI*shift)/6;
                double x = Math.sin(latitude) * Math.cos(longitude);
                double y = Math.cos(latitude);
                double z = Math.sin(latitude) * Math.sin(longitude);
                points.add(
                    co.getHandle().rotform(
                        new Point3D(
                            r * x, 
                            r * y, 
                            r * z
                        ).add(co.getPosition())
                    )
                );
            }
        }
        return points;
    }

    /**
     * since the length vector of a line charge is locally +x, this will be too
     */
    private List<Point3D> pointsOnCylinder(Point3D center, double r, int n, 
        int shift, LineCharge lc) {
        List<Point3D> points = new ArrayList<>();
        for (double i = -1; i <= n+1; i++) {
            for (int j = 0; j < n; j++) {
                double z = Math.cos(((2*Math.PI*j)/n)+shift);
                double y = Math.sin(((2*Math.PI*j)/n)+shift);
                points.add(
                    lc.getHandle().rotform(
                        new Point3D(
                            (i/n)*lc.getL().magnitude(), 
                            (i/n < 0 || i/n > 1 ? 0.33*r : r) * y, 
                            (i/n < 0 || i/n > 1 ? 0.33*r : r) * z
                        ).add(lc.getPosition())
                    )
                );
            }
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