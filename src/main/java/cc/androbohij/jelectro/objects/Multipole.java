package cc.androbohij.jelectro.objects;

import static cc.androbohij.jelectro.Settings.K;
import static cc.androbohij.jelectro.Settings.MU_NOUGHT;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;


public class Multipole extends ChargedObject {

    private List<PointCharge> charges;
    private double chargePer;

    public Multipole(Point3D position, double q, 
        double rx, double ry, double rz, boolean child, int poles, double s) {
        super(position, q, rx, ry, rz, child);
        charges = new ArrayList<>();
        chargePer = q;
        createMultipole(poles, chargePer, s);
    }

    public Point3D getE(Point3D p) {
        // return getESumless(p);
        Point3D SumEVec = Point3D.ZERO;
        for (ChargedObject chargedObject : charges) {
            SumEVec = SumEVec.add(chargedObject.getE(p));
        }
        return SumEVec;
    }

    // private Point3D getESumless(Point3D point) {
    //     Point3D p = charges.get(0).getPosition()
    //         .subtract(charges.get(1).getPosition()).multiply(chargePer);
    //     Point3D rhat = point.subtract(getPosition()).normalize();
    //     Point3D nom = rhat
    //         .multiply(p.dotProduct(rhat)).multiply(3)
    //         .subtract(p);
    //     return nom.multiply(1.0 / Math.pow(getPosition().distance(point), 3))
    //         .multiply(K);
    // }

    @Override
    public Point3D getB(Point3D p) {
        return getBSumless(p);
        // Point3D SumBVec = Point3D.ZERO;
        // for (ChargedObject chargedObject : charges) {
        //     SumBVec = SumBVec.add(((PointCharge)chargedObject).getB(p, getVelocity()));
        // }
        // return SumBVec;
    }

    private Point3D getBSums(Point3D point) {
        Point3D SumBVec = Point3D.ZERO;
        for (ChargedObject chargedObject : charges) {
            chargedObject.setVelocity(getVelocity());
            SumBVec = SumBVec.add(((PointCharge)chargedObject).getB(point));
        }
        return SumBVec;
    }

    /**
     * the proper magnetic dipole equation, but its different than what i get from superposition
     */
    private Point3D getBSumless(Point3D point) {
        Point3D p = charges.get(1).getPosition()
            .subtract(charges.get(0).getPosition()).multiply(chargePer);
        Point3D m = getVelocity().crossProduct(p);
        Point3D rhat = point.subtract(getPosition()).normalize();
        Point3D nom = rhat
            .multiply(m.dotProduct(rhat)).multiply(3)
            .subtract(m);
        return nom.multiply(1.0 / Math.pow(getPosition().distance(point), 3))
            .multiply(mu_nought/(4.0*Math.PI));
    }

    /**
     * from http://kirkmcd.princeton.edu/examples/movingdipole.pdf
     * it produces a shape that is like a the cross-section magnetic dipole, but extruded along v
     * i thought for a while this was wrong but the math does seem to check out
     */
    // private Point3D getBSumless(Point3D point) {
    //     // TODO maybe separate dipole class that also uses the coordinate-free E equation
    //     // maybe keeping multipole around as a parent class
    //     Point3D p = charges.get(1).getPosition()
    //         .subtract(charges.get(0).getPosition()).multiply(chargePer);

    //     Point3D rhat = point.subtract(getPosition()).normalize();
    //     Point3D nom = getVelocity().crossProduct(rhat)
    //         .multiply(p.dotProduct(rhat)).multiply(3)
    //         .subtract(getVelocity().crossProduct(p));
    //     return nom.multiply(1.0 / Math.pow(getPosition().distance(point), 3))
    //         .multiply(mu_nought/(4.0*Math.PI));
    // }

    @Override
    public void setCharge(double q) {
        for (int i = 0; i < charges.size(); i++) {
            charges.get(i).setCharge((i % 2 == 0) ? q : -q);
        }
    }

    public List<PointCharge> getCharges() {
        return charges;
    }

    private void addCharge(PointCharge pointCharge) {
        pointCharge.setVelocity(getVelocity());
        charges.add(pointCharge);
        getHandle().getChildren().add(pointCharge.getHandle());
    }

    private void createMultipole(int poles, double q, double d) {
        for (int i = 0; i < poles; i++) {
            addCharge(new PointCharge(
                new Point3D(xAboutD(poles, i, d), 0, zAboutD(poles, i, d)),
                (i % 2 == 0) ? q : -q,
                true
            ));
        }
    }

    private double xAboutD(int poles, int i, double s) {
       return ((s/2)*Math.cos((2*Math.PI*i)/(double)poles)); 
    }

    private double zAboutD(int poles, int i, double s) {
        return ((s/2)*Math.sin((2*Math.PI*i)/(double)poles));
    }
    
}
