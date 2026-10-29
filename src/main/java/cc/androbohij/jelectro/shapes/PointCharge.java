package cc.androbohij.jelectro.shapes;

import static cc.androbohij.jelectro.Settings.K;
import javafx.geometry.Point3D;

public class PointCharge extends ChargedObject {

    public PointCharge(Point3D position, double charge) {
        super(position, charge);
    }

    public Point3D getE(Point3D p) {
        Point3D sepVector = p.subtract(position).normalize();
        double r = position.distance(p);
        return sepVector.multiply((charge*K)/(Math.pow(r, 2)));
    }
    
}
