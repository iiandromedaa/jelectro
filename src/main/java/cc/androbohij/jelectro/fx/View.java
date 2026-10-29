package cc.androbohij.jelectro.fx;

import java.util.logging.Logger;

import cc.androbohij.jelectro.shapes.ChargedObject;
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
import javafx.scene.shape.CullFace;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class View extends Application {

    private static final Logger log = Logger.getLogger(View.class.getName());
    private Group root;
    private Camera camera;

    private double mouseDownX;
    private double mouseDownY;

    private Rotate camPitch;
    private Rotate camYaw;

    public View() {
        root = new Group();
        camera = new PerspectiveCamera(true);
        camPitch = new Rotate(0, Rotate.X_AXIS);
        camYaw = new Rotate(0, Rotate.Y_AXIS);
    }

    @Override
    public void start(Stage stage) {
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            log.warning("3d is not supported");
            return;
        }
        Scene scene = new Scene(root, 640, 480, true);

        Box mousePlane = new Box(1,1,1);
        mousePlane.setMaterial(new PhongMaterial(new Color(1, 0, 0, 1)));
        root.getChildren().add(mousePlane);

        camera.setTranslateZ(-10);
        camera.setFarClip(10000);
        Group cameraPivot = new Group(camera);
        cameraPivot.getTransforms().addAll(camYaw, camPitch);
        
        scene.setCamera(camera);
        scene.setOnMousePressed(this::mousePressed);
        scene.setOnMouseDragged(this::mouseDrag);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    private void addObject(ChargedObject object) {

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

        camPitch.setAngle(camPitch.getAngle() - dy);
        camYaw.setAngle(camYaw.getAngle() + dx);

        mouseDownX = event.getSceneX();
        mouseDownY = event.getSceneY();
    }

}