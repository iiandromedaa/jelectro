package cc.androbohij.jelectro;

import java.util.ArrayList;
import java.util.List;

import cc.androbohij.jelectro.shapes.ChargedObject;
import javafx.geometry.Point3D;

public class EField {

    List<ChargedObject> objects;

    public EField() {
        objects = new ArrayList<>();
    }

    public void createObject() {

    }

    public Point3D getEAtPoint(Point3D position) {
        Point3D SumEVec = Point3D.ZERO;

        return SumEVec;
    }

}