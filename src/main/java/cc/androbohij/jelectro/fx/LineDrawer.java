package cc.androbohij.jelectro.fx;

import static cc.androbohij.jelectro.Settings.vectorDensity;

import java.util.List;

import static cc.androbohij.jelectro.Settings.cullFieldVecs;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class LineDrawer {
    
    public static Group draw(Point3D p1, Point3D p2, boolean vector) {
        Point3D directionVec = p2.subtract(p1);
        double length = directionVec.magnitude();

        double scale = vector ? Math.min(length, 1/vectorDensity) / length : 1.0;

        Point3D scaledVec = directionVec.multiply(scale);
        double scaledLength = scaledVec.magnitude();
        Point3D mid = p1.add(scaledVec.multiply(0.5));

        if (length == 0 || scaledLength < cullFieldVecs)
            return new Group();

        PhongMaterial mat = new PhongMaterial(Color.WHITE);
        mat.setSelfIlluminationMap(new Image(
            LineDrawer.class.getClassLoader().getResource("selfIllum.png").toExternalForm()));

        // its secretly just really thin quads
        Line3D strip = new Line3D(0.0001f, (float)scaledLength, true);
        strip.setCullFace(CullFace.NONE);
        strip.setMaterial(mat);
        // plus wireframe mode so its always the same width onscreen
        strip.setDrawMode(DrawMode.LINE);

        Group line = new Group(strip);

        line.getTransforms().addAll(
            new Translate(mid.getX(), mid.getY(), mid.getZ()),
            new Rotate(-scaledVec.angle(Rotate.Y_AXIS),
                scaledVec.crossProduct(Rotate.Y_AXIS))
        );
        line.setMouseTransparent(true);
        return line;
    }

    public static Group draw(Point3D p1, Point3D p2) {
        return draw(p1, p2, false);
    }

    public static Group draw(List<Point3D> path) {
        Group lines = new Group();
        for (int i = 0; i < path.size()-1; i++) {
            lines.getChildren().add(draw(path.get(i), path.get(i+1), false));
        }
        return lines;
    }

}
