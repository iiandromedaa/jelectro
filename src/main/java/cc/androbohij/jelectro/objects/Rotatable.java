package cc.androbohij.jelectro.objects;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;

public abstract class Rotatable extends ChargedObject {

    protected Rotate rotx;
    protected Rotate roty;
    protected Rotate rotz;

    public Rotatable(Point3D position, double charge, double rx, double ry, double rz) {
        super(position, charge);
        rotx = new Rotate(rx, position.getX(), position.getY(), position.getZ(), Rotate.X_AXIS);
        roty = new Rotate(ry, position.getX(), position.getY(), position.getZ(), Rotate.Y_AXIS);
        rotz = new Rotate(rz, position.getX(), position.getY(), position.getZ(), Rotate.Z_AXIS);
    }
    
}
