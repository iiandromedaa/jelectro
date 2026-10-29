package cc.androbohij.jelectro.shapes;

import javafx.geometry.Point3D;

public abstract class ChargedObject {
    
    Point3D position;
    double charge;

    public ChargedObject(Point3D position, double charge) {
        this.position = position;
        this.charge = charge;
    }

    /**
     * this returns a vector, it may say Point3D, but it's a vector
     * @param p point we are measuring E at
     * @return a vector for E at that point p
     */
    public abstract Point3D getE(Point3D p);

}
