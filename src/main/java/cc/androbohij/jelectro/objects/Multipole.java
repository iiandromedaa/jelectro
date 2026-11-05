package cc.androbohij.jelectro.objects;

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
        Point3D SumEVec = Point3D.ZERO;
        for (ChargedObject chargedObject : charges) {
            SumEVec = SumEVec.add(chargedObject.getE(p));
        }
        return SumEVec;
    }

    @Override
    public Point3D getB(Point3D p) {
        Point3D SumBVec = Point3D.ZERO;
        for (ChargedObject chargedObject : charges) {
            chargedObject.setVelocity(getVelocity());
            SumBVec = SumBVec.add(chargedObject.getB(p));
        }
        return SumBVec;
    }

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
