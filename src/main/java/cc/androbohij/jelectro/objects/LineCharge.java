package cc.androbohij.jelectro.objects;

import static cc.androbohij.jelectro.Settings.K;

import cc.androbohij.jelectro.fx.LineDrawer;
import javafx.geometry.Point3D;

public class LineCharge extends ChargedObject {

    private Point3D L;
    private double l;

    public LineCharge(Point3D position, double charge, 
        double rx, double ry, double rz, double length) {
        super(position, charge, rx, ry, rz);
        l = length;
        L = new Point3D(l, 0, 0);
        getHandle().rotform(L);
        setCharge(charge);
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
        if (r.magnitude() < 0.01)
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
        getHandle().getChildren().clear();
        if (q > 0)
            getHandle().getChildren().add(LineDrawer.draw(Point3D.ZERO, L, LineDrawer.RED));
        else if (q < 0)
            getHandle().getChildren().add(LineDrawer.draw(Point3D.ZERO, L, LineDrawer.BLUE));
        else
            getHandle().getChildren().add(LineDrawer.draw(Point3D.ZERO, L, LineDrawer.WHITE));
    }
    
}
