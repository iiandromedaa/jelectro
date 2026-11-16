package cc.androbohij.jelectro.objects;

import static cc.androbohij.jelectro.Settings.K;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class LineCharge extends ChargedObject {

    private Point3D L;
    private Cylinder cylinder;
    private double l;

    public LineCharge(Point3D position, double charge, 
        double rx, double ry, double rz, double length) {
        super(position, charge, rx, ry, rz);
        l = length;
        L = new Point3D(l, 0, 0);
        cylinder = new Cylinder(0.05, length, 7);
        cylinder.getTransforms().addAll(
            new Translate(l/2, 0, 0),
            new Rotate(90, Rotate.Z_AXIS)
        );
        if (charge > 0)
            cylinder.setMaterial(new PhongMaterial(Color.RED));
        else if (charge < 0)
            cylinder.setMaterial(new PhongMaterial(Color.DEEPSKYBLUE));
        getHandle().rotform(L);
        getHandle().add(cylinder);
    }

    /**
     * from https://doi.org/10.1119/1.2348889
     * i was so glad to have found an article that actually
     * covers the coordinate-free form of a finite line charge
     * useful derived equations for these generalized cases
     * are SURPRISINGLY hard to find
     */
    public Point3D getE(Point3D p) {
        Point3D r = p.subtract(getPosition());
        // its r+L in the paper but something about my sign conventions requires - here
        Point3D rplus = r.subtract(L);
        if (r.magnitude() < 0.05)
            return Point3D.ZERO;
        return
            (rplus.normalize().add(r.normalize())).multiply(
                (L.magnitude()*2*K*(getCharge()/L.magnitude()))
                / (Math.pow((r.magnitude() + rplus.magnitude()), 2)
                    - Math.pow(L.magnitude(), 2))
            );
    }

    @Override
    public void rotate(double rx, double ry, double rz) {
        getHandle().rotate(rx, ry, rz);
        L = getHandle().rotform(new Point3D(l, 0, 0));
    }

    @Override
    public void setCharge(double q) {
        super.setCharge(q);
        if (q > 0)
            cylinder.setMaterial(new PhongMaterial(Color.RED));
        else if (q < 0)
            cylinder.setMaterial(new PhongMaterial(Color.DEEPSKYBLUE));
        else
            cylinder.setMaterial(new PhongMaterial());
    }

    @Override
    public double getSurfaceDistance(Point3D p) {
        return ((p.subtract(getPosition())).crossProduct(L)).magnitude() 
            / L.magnitude();
    }

    public Point3D getL() {
        return L;
    }
    
}
