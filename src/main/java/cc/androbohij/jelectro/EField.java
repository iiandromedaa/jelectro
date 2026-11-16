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

    public List<StreamlinePoint> getStreamline(Point3D start, int steps, double stepSize,
        boolean fwd, Function<Point3D, Point3D> field) {
        ArrayList<StreamlinePoint> path = new ArrayList<>();
        Point3D p = start;
        int sign = fwd ? 1 : -1;

        double currentStep = stepSize;
        double maxStep = stepSize * 10;

        Point3D previousDirection = null;

        for (int i = 0; i < steps; i++) {
            Point3D em = field.apply(p);

            // System.out.println(em.magnitude());
            path.add(new StreamlinePoint(p, Math.min(em.magnitude(), 1)));
            double minR = Double.MAX_VALUE;
            for (ChargedObject chargedObject : objects) {
                double r = chargedObject.getSurfaceDistance(p);
                minR = Math.min(r, minR);
                if (r < 0.05) {
                    if (!fwd)
                        Collections.reverse(path);
                    return path;
                }
            }
            
            // we gotta accept really small values because at these speeds B is tiny
            // + checking if the curl brought us back where we started
            // because my integrator is undershooting somehow so it spirals inwards if you let it
            if (em.magnitude() < 1e-25 || minR > 3.5 ||
                (previousDirection != null && start.distance(p) < stepSize*0.95)) {
                if (!fwd)
                    Collections.reverse(path);
                return path;
            }

            // current step
            Point3D direction1 = em.normalize().multiply(sign*0.5);
            Point3D pPredictor = p.add(direction1.multiply(currentStep));
            
            // next step's direction
            Point3D direction2 = field.apply(pPredictor).normalize().multiply(sign*0.5);

            // avg directions between step n and n+1
            Point3D direction = direction1.add(direction2).normalize();

            if (previousDirection != null) {
                double alignment = previousDirection.dotProduct(direction);
                if (alignment > 0.99) {
                    currentStep = Math.min(currentStep * 1.2, maxStep);
                } else if (alignment < 0.99) {
                    currentStep = Math.max(currentStep * 0.3, stepSize);
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