package cc.androbohij.jelectro.objects;

import static cc.androbohij.jelectro.Settings.K;
import static cc.androbohij.jelectro.Settings.MU_NOUGHT;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;


public class Dipole extends ChargedObject {

    private Point3D p;
    private double s;
    private double q;

    public Dipole(Point3D position, double q, 
        double rx, double ry, double rz, double s) {
        super(position, q, rx, ry, rz);
        this.q = q;
        this.s = s;
        p = new Point3D(s, 0, 0).multiply(q);
        p = getHandle().rotform(p);
        setUpDipole();
    }

    public Point3D getE(Point3D point) {
        return getESumless(point);
    }

    private Point3D getESumless(Point3D point) {
        Point3D rhat = point.subtract(getPosition()).normalize();
        Point3D nom = rhat
            .multiply(p.dotProduct(rhat)).multiply(3)
            .subtract(p);
        return nom.multiply(1.0 / Math.pow(getPosition().distance(point), 3))
            .multiply(K);
    }

    @Override
    public Point3D getB(Point3D p) {
        return getBSumless(p);
    }

    /**
     * from http://kirkmcd.princeton.edu/examples/movingdipole.pdf
     * it produces a shape that is like a the cross-section magnetic dipole, but extruded along v
     * i thought for a while this was wrong but the math does seem to check out
     */
    private Point3D getBSumless(Point3D point) {
        Point3D rhat = point.subtract(getPosition()).normalize();
        Point3D nom = getVelocity().crossProduct(rhat)
            .multiply(p.dotProduct(rhat)).multiply(3)
            .subtract(getVelocity().crossProduct(p));
        return nom.multiply(1.0 / Math.pow(getPosition().distance(point), 3))
            .multiply(MU_NOUGHT/(4.0*Math.PI));
    }

    private void setUpDipole() {
        getHandle().getChildren().clear();
        for (int i = 0; i < 2; i++) {
            Sphere sp = new Sphere(0.1);
            if (i % 2 == (q > 0 ? 0 : 1)) {
                sp.translateXProperty().set((i % 2 == 0 ? s : -s));
                sp.setMaterial(new PhongMaterial(q == 0 ? Color.WHITE : Color.RED));
            } else {
                sp.translateXProperty().set((i % 2 == 0 ? s : -s));
                sp.setMaterial(new PhongMaterial(q == 0 ? Color.WHITE : Color.DEEPSKYBLUE));
            }
            getHandle().getChildren().add(sp);
        }
    }

    @Override
    public void rotate(double rx, double ry, double rz) {
        getHandle().rotate(rx, ry, rz);
        p = getHandle().rotform(new Point3D(s, 0, 0).multiply(q));
    }

    @Override
    public void setCharge(double q) {
        if (p.equals(Point3D.ZERO))
            p = getHandle().rotform(new Point3D(s, 0, 0).multiply(q));
        else
            p = p.multiply(1.0 / this.q).multiply(q);
        this.q = q;
        setUpDipole();
    }

}
