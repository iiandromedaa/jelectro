package cc.androbohij.jelectro.fx;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Handle extends Group {
    
    public final Translate translate = new Translate();
    public final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    public final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    public final Rotate rz = new Rotate(0, Rotate.Z_AXIS);

    public Handle() {
        super();
        getTransforms().addAll(translate, rz, ry, rx);
    }

    public void translate(double x, double y, double z) {
        translate.setX(x);
        translate.setY(y);
        translate.setZ(z);
    }

    public void rotate(double x, double y, double z) {
        rx.setAngle(x);
        ry.setAngle(y);
        rz.setAngle(z);
    }

}
