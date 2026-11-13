package cc.androbohij.jelectro.objects;

import javafx.geometry.Point3D;

public class PlaneCharge extends ChargedObject {

    private double area;

    public PlaneCharge(Point3D position, double charge, 
        double rx, double ry, double rz, double area) {
        super(position, charge, rx, ry, rz);
        //TODO Auto-generated constructor stub
    }

    public Point3D getE(Point3D p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getE'");
    }

    public Point3D getB(Point3D p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getB'");
    }
    
}
