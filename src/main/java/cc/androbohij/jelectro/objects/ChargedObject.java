package cc.androbohij.jelectro.objects;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;

public abstract class ChargedObject {
    
    private Point3D position;
    private double charge;
    private Group shape;

    public ChargedObject(Point3D position, double charge) {
        this.position = position;
        this.charge = charge;
        this.shape = new Group();
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
        return position;
    }

    public Group getShape() {
        return shape;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    protected void addShape(Node node) {
        shape.getChildren().add(node);
    }

}
