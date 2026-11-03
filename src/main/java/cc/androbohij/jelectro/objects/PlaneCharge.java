package cc.androbohij.jelectro.objects;

import javafx.geometry.Point3D;

public class PlaneCharge extends ChargedObject {

    private double area;

    public PlaneCharge(Point3D position, double charge) {
        super(position, charge);
        //TODO Auto-generated constructor stub
    }

    @Override
    public Point3D getE(Point3D p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getE'");
    }
    
}
