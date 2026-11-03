package cc.androbohij.jelectro.objects;

import cc.androbohij.jelectro.fx.Handle;
import javafx.geometry.Point3D;
import javafx.scene.Node;

public abstract class ChargedObject {

    private double charge;
    private Handle handle;
    private boolean child;

    // TODO: BUILDER PATTERN !!!!!!!!!!!!
    // TODO: although with the subclasses off this, might require CRTP
    public ChargedObject(Point3D position, double charge, 
        double rx, double ry, double rz, boolean child) {
        handle = new Handle();
        handle.translate(position);
        handle.rotate(rx, ry, rz);
        this.charge = charge;
        this.child = child;
    }

    public ChargedObject(Point3D position, double charge, boolean child) {
        this(position, charge, 0, 0, 0, child);
    }

    public ChargedObject(Point3D position, double charge) {
        this(position, charge, 0, 0, 0, false);
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
        if (child)
            return getHandle().localToScene(getHandle().getPosition());
        else
            return handle.getPosition();
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
