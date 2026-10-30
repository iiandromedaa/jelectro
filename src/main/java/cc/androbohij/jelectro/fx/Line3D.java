package cc.androbohij.jelectro.fx;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Line3D extends MeshView {
    
    public Line3D(float width, float height, boolean arrow) {
        TriangleMesh strip = new TriangleMesh();

        float[] points = {
            -width/2, height/2, 0f,
            -width/2, -height/2, 0f,
            width/2, height/2, 0f,
            width/2, -height/2, 0f
        };

        float[] arrowPoints = {
            (width/2), height/2, 0f,
            (width/2)+0.025f, (height/2)-0.025f, 0f,
            (width/2), height/2, 0f,

            (width/2), height/2, 0f,
            (width/2)-0.025f, (height/2)-0.025f, 0f,
            (width/2), height/2, 0f
        };

        float[] uv = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
        };

        float[] arrowUV = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f
        };

        int[] faces = {
            0, 0,  1, 1,  2, 2,
            2, 2,  1, 1,  3, 3
        };

        int[] arrowFaces = {
            4, 4, 5, 5, 6, 6,
            6, 4, 7, 5, 8, 6
        };

        strip.getPoints().addAll(points);
        strip.getTexCoords().addAll(uv);
        strip.getFaces().addAll(faces);
        
        if (arrow) {
            strip.getPoints().addAll(arrowPoints);
            strip.getTexCoords().addAll(arrowUV);
            strip.getFaces().addAll(arrowFaces);
        }

        setMesh(strip);
    }

    public Line3D(float width, float height) {
        this(width, height, false);
    }

}
