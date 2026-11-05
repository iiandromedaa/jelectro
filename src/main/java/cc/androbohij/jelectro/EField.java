package cc.androbohij.jelectro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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
        if (!objects.remove(chargedObject))
            return;
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

    public Point3D getBAtPoint(Point3D position) {
        Point3D SumBVec = Point3D.ZERO;
        for (ChargedObject chargedObject : objects) {
            SumBVec = SumBVec.add(chargedObject.getB(position));
        }
        return SumBVec;
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

    public List<Point3D> getStreamline(Point3D start, int steps, double stepSize, 
        boolean fwd, Function<Point3D, Point3D> field) {
        ArrayList<Point3D> path = new ArrayList<>();
        Point3D p = start;
        int sign = fwd ? 1 : -1;

        double currentStep = stepSize;
        double maxStep = stepSize * 15;

        Point3D previousDirection = null;

        for (int i = 0; i < steps; i++) {
            path.add(p);
            double minR = Double.MAX_VALUE;
            for (ChargedObject chargedObject : objects) {
                double r = p.distance(chargedObject.getPosition());
                minR = Math.min(r, minR);
                if (r < 0.1) {
                    if (!fwd)
                        Collections.reverse(path);
                    return path;
                }
            }
            Point3D e = field.apply(p);
            
            if (e.magnitude() < 0.001 || minR > 3.5) {
                if (!fwd)
                    Collections.reverse(path);
                return path;
            }

            Point3D direction = e.normalize().multiply(sign);

            if (previousDirection != null) {
                double alignment = previousDirection.dotProduct(direction);
                if (alignment > 0.99) {
                    currentStep = Math.min(currentStep * 1.2, maxStep);
                } else if (alignment < 0.99) {
                    currentStep = Math.max(currentStep * 0.5, stepSize);
                }
            }

            previousDirection = direction;
            p = p.add(direction.multiply(currentStep));
        }

        if (!fwd)
            Collections.reverse(path);
        return path;
    }

}