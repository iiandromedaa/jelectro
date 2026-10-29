package cc.androbohij.jelectro.shapes;

import static cc.androbohij.jelectro.Settings.K;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class PointCharge extends ChargedObject {

    public PointCharge(Point3D position, double charge) {
        super(position, charge);
        shape = new Sphere(0.1);
        shape.setTranslateX(position.getX());
        shape.setTranslateY(position.getY());
        shape.setTranslateZ(position.getZ());
        if (charge > 0)
            shape.setMaterial(new PhongMaterial(Color.RED));
        else if (charge < 0)
            shape.setMaterial(new PhongMaterial(Color.DEEPSKYBLUE));
    }

    public Point3D getE(Point3D p) {
        Point3D sepVector = p.subtract(position).normalize();
        double r = position.distance(p);
        return sepVector.multiply((charge*K)/(Math.pow(r, 2)));
    }
    
}
