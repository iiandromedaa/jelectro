package cc.androbohij.jelectro.objects;

import static cc.androbohij.jelectro.Settings.EPSILON_NOUGHT;
import static cc.androbohij.jelectro.Settings.MU_NOUGHT;

import cc.androbohij.jelectro.fx.Handle;
import javafx.geometry.Point3D;
import javafx.scene.Node;

public abstract class ChargedObject {

    private double charge;
    private final Handle handle;
    private Point3D velocity;

    // TODO: BUILDER PATTERN !!!!!!!!!!!!
    // TODO: although with the subclasses off this, might require CRTP
    public ChargedObject(Point3D position, double charge, 
        double rx, double ry, double rz) {
        velocity = Point3D.ZERO; 
        handle = new Handle(this);
        handle.translate(position);
        handle.rotate(rx, ry, rz);
        this.charge = charge;
    }

    /**
     * this returns a vector, it may say Point3D, but it's a vector
     * @param p point we are measuring E at
     * @return a vector for E at that point p
     */
    public abstract Point3D getE(Point3D p);

    public Point3D getB(Point3D p) {
        return getVelocity().crossProduct(getE(p))
            .multiply(MU_NOUGHT)
            .multiply(EPSILON_NOUGHT);
    };

    public double getCharge() {
        return charge;
    }

    public Point3D getPosition() {
        return handle.getPosition();
    }

    public double getPositionX() {
        return getPosition().getX();
    }

    public double getPositionY() {
        return getPosition().getY();
    }

    public double getPositionZ() {
        return getPosition().getZ();
    }

    public Point3D getVelocity() {
        return velocity;     
    }

    public void setVelocity(Point3D velocity) {
        this.velocity = velocity;
    }

    public double getRx() {
        return handle.rx.getAngle();
    }

    public double getRy() {
        return handle.ry.getAngle();
    }

    public double getRz() {
        return handle.rz.getAngle();
    }

    public Handle getHandle() {
        return handle;
    }

    public void setCharge(double q) {
        this.charge = q;
    }

    public void setPosition(Point3D position) {
        handle.translate(position);
    }

    public void rotate(double rx, double ry, double rz) {
        handle.rotate(rx, ry, rz);
    }

    protected void addShape(Node node) {
        handle.getChildren().add(node);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" ");
        sb.append(hashCode());
        return sb.toString();
    }

}
