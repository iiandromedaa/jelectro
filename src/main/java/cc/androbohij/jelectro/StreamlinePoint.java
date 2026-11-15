package cc.androbohij.jelectro;

import javafx.geometry.Point3D;

public class StreamlinePoint {
    
    private final Point3D point;
    private double value;

    StreamlinePoint(Point3D point, double opacity) {
        this.point = point;
        this.value = opacity;
    }

    public Point3D getPoint() {
        return point;
    }

    public double getValue() {
        return value;
    }

    public void scaleValue(double scale) {
        value = Math.min(value*scale, 1);
    }

}
