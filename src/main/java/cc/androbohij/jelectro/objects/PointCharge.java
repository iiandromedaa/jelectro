package cc.androbohij.jelectro.objects;

import static cc.androbohij.jelectro.Settings.K;
import static cc.androbohij.jelectro.Settings.mu_nought;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class PointCharge extends ChargedObject {

    Sphere sphere;

    public PointCharge(Point3D position, double charge, boolean child) {
        super(position, charge, child);
        sphere = new Sphere(0.1);
        if (charge > 0)
            sphere.setMaterial(new PhongMaterial(Color.RED));
        else if (charge < 0)
            sphere.setMaterial(new PhongMaterial(Color.DEEPSKYBLUE));
        addShape(sphere);
    }
    
    public PointCharge(Point3D position, double charge) {
        this(position, charge, false);
    }

    public Point3D getE(Point3D p) {
        double r = getPosition().distance(p);
        if (r < 0.01)
            return Point3D.ZERO;
        Point3D sepVector = p.subtract(getPosition()).normalize();
        return sepVector.multiply((getCharge()*K)/(Math.pow(r, 2)));
    }

    public Point3D getB(Point3D p) {
        double r = getPosition().distance(p);
        if (r < 0.01)
            return Point3D.ZERO;
        Point3D sepVector = p.subtract(getPosition()).normalize();
        Point3D step1 = getVelocity().multiply(getCharge())
        .crossProduct(sepVector).multiply((1/(Math.pow(r, 2))));
        return step1.multiply(3e8);
    }

    @Override
    public void setCharge(double q) {
        super.setCharge(q);
        if (q > 0)
            sphere.setMaterial(new PhongMaterial(Color.RED));
        else if (q < 0)
            sphere.setMaterial(new PhongMaterial(Color.DEEPSKYBLUE));
        else
            sphere.setMaterial(new PhongMaterial());
    }
    
}
