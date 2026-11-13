package cc.androbohij.jelectro.fx;

import cc.androbohij.jelectro.objects.ChargedObject;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Handle extends Group {
    
    public final Translate translate = new Translate();
    public final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    public final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    public final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
    public final ChargedObject parent;

    public Handle(ChargedObject parent) {
        super();
        this.parent = parent;
        getTransforms().addAll(translate, rx, ry, rz);
    }

    public void translate(double x, double y, double z) {
        translate.setX(x);
        translate.setY(y);
        translate.setZ(z);
    }

    public void translate(Point3D position) {
        translate.setX(position.getX());
        translate.setY(position.getY());
        translate.setZ(position.getZ());
    }

    public void rotate(double x, double y, double z) {
        rx.setAngle(x);
        ry.setAngle(y);
        rz.setAngle(z);
    }

    public void add(Node node) {
        getChildren().add(node);
    }

    public Point3D getPosition() {
        return new Point3D(translate.getX(), translate.getY(), translate.getZ());
    }

    public Point3D rotform(Point3D p) {
        return rx.transform(
            ry.transform(
                rz.transform(p)
            )
        );
    }

}
