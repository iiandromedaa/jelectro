package cc.androbohij.jelectro.objects;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;

public class Multipole extends ChargedObject {

    private List<PointCharge> charges;

    public Multipole(Point3D position, double q, double d, int poles, 
        double rx, double ry, double rz) {
        super(position, 0, rx, ry, rz);
        charges = new ArrayList<>();
        createMultipole(poles, q, d);
    }

    public Point3D getE(Point3D p) {
        Point3D SumEVec = Point3D.ZERO;
        for (ChargedObject chargedObject : charges) {
            SumEVec = SumEVec.add(chargedObject.getE(p));
        }
        return SumEVec;
    }

    public List<PointCharge> getCharges() {
        return charges;
    }

    private void addCharge(PointCharge pointCharge) {
        charges.add(pointCharge);
        getHandle().getChildren().add(pointCharge.getHandle());
        // System.out.println(pointCharge.getPosition());
        // System.out.println(getPosition());
    }

    @Override
    public Point3D getPosition() {
        // System.out.println(getHandle().getPosition());
        return getHandle().getPosition();
    }

    private void createMultipole(int poles, double q, double d) {
        for (int i = 0; i < poles; i++) {
            addCharge(new PointCharge(
                // getHandle().rz.transform(
                    // getHandle().ry.transform(
                        // getHandle().rx.transform(
                            new Point3D(xAboutD(poles, i, d), 0, zAboutD(poles, i, d)),
                        // )
                    // )
                // ), 
                (i % 2 == 0) ? q : -q
            ));
        }
    }

    private double xAboutD(int poles, int i, double d) {
       return ((d/2)*Math.cos((2*Math.PI*i)/(double)poles)); 
    }

    private double zAboutD(int poles, int i, double d) {
        return ((d/2)*Math.sin((2*Math.PI*i)/(double)poles));
    }
    
}
