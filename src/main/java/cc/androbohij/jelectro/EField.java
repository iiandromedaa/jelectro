package cc.androbohij.jelectro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.androbohij.jelectro.objects.ChargedObject;
import javafx.geometry.Point3D;

public class EField {

    private List<ChargedObject> objects;
    private List<ListObserver> observers;
    private static EField instance;

    private EField() {
        objects = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public static EField getInstance() {
        if (instance == null)
            instance = new EField();
        return instance;
    }

    public void createObject(ChargedObject chargedObject) {
        objects.add(chargedObject);
        for (ListObserver observer : observers) {
            observer.listAdd(chargedObject);
        }
    }

    public void removeObject(ChargedObject chargedObject) {
        objects.remove(chargedObject);
        for (ListObserver observer : observers) {
            observer.listRemove(chargedObject);
        }
    }

    public void clearObjects() {
        objects.clear();
        for (ListObserver observer : observers) {
            observer.listClear();
        }
    }

    public Point3D getEAtPoint(Point3D position) {
        Point3D SumEVec = Point3D.ZERO;
        for (ChargedObject chargedObject : objects) {
            SumEVec = SumEVec.add(chargedObject.getE(position));
        }
        return SumEVec;
    }

    public void subscribe(ListObserver observer) {
        observers.add(observer);
    }

    public List<ChargedObject> getObjects() {
        return objects;
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public List<Point3D> getStreamline(Point3D start, int steps, double stepSize, boolean fwd) {
        ArrayList<Point3D> path = new ArrayList<>();
        Point3D p = start;
        int sign = fwd ? 1 : -1;

        for (int i = 0; i < steps; i++) {
            path.add(p);
            for (ChargedObject chargedObject : objects) {
                double r = p.distance(chargedObject.getPosition());
                if (r < 0.1 || r > 3.5) {
                    if (!fwd)
                        Collections.reverse(path);
                    return path;
                }
            }
            Point3D e = getEAtPoint(p);
            
            if (e.magnitude() < 0.001) {
                if (!fwd)
                    Collections.reverse(path);
                return path;
            }

            p = p.add(e.normalize().multiply(stepSize * sign));
        }

        if (!fwd)
            Collections.reverse(path);
        return path;
    }

}