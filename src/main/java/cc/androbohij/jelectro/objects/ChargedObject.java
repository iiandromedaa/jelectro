package cc.androbohij.jelectro.objects;

import cc.androbohij.jelectro.fx.Handle;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;

public abstract class ChargedObject {

    private double charge;
    private Handle handle;

    public ChargedObject(Point3D position, double charge, double rx, double ry, double rz) {
        handle = new Handle();
        System.out.println(position);
        handle.translate(position);
        handle.rotate(rx, ry, rz);
        this.charge = charge;
    }

    public ChargedObject(Point3D position, double charge) {
        this(position, charge, 0, 0, 0);
    }

    /**
     * this returns a vector, it may say Point3D, but it's a vector
     * @param p point we are measuring E at
     * @return a vector for E at that point p
     */
    public abstract Point3D getE(Point3D p);

    public double getCharge() {
        return charge;
    }

    public Point3D getPosition() {
        System.out.println(handle.localToScene(handle.getPosition()));
        return handle.localToScene(handle.getPosition());
    }

    public Handle getHandle() {
        return handle;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public void setPosition(Point3D position) {
        handle.translate(position);
    }

    protected void addShape(Node node) {
        handle.getChildren().add(node);
    }

}
