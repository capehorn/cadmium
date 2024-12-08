package capehorn.cadmium.io;

import capehorn.cadmium.core.Vec3;
import capehorn.cadmium.geom.CubicBezierCurve;
import capehorn.cadmium.geom.Frame;
import capehorn.cadmium.geom.Segment;
import capehorn.cadmium.geom.Triangle;
import capehorn.cadmium.geom.mesh.TriangleMesh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GeomToObjFormatTest {

    @Test
    void writeTriangleMesh() throws IOException {
        StringWriter sw = new StringWriter();
        GeomToObjFormat toObjFormat = new GeomToObjFormat(sw);
        var p1 = Vec3.of(0, 0, 0);
        var p2 = Vec3.of(1, 0, 0);
        var p3 = Vec3.of(1, 1, 0);
        var p4 = Vec3.of(0, 1, 0);
        TriangleMesh mesh = new TriangleMesh();
        mesh.addTriangles(new Triangle(p1, p2, p3), new Triangle(p3, p4, p1));
        toObjFormat.writeTriangleMesh(mesh);
        sw.flush();
        String objFormat = sw.toString();
        writeToTempFile("obj-tringlemesh-", ".obj", objFormat);
    }

    @Test
    void writeParametricCurve() throws IOException {
        StringWriter sw = new StringWriter();
        GeomToObjFormat toObjFormat = new GeomToObjFormat(sw);
        CubicBezierCurve curve = new CubicBezierCurve(
                Vec3.of(0, 0, 0),
                Vec3.of(100, 0, 0),
                Vec3.of(100, 100, 0),
                Vec3.of(0, 0, 100)
        );


        int numOfPoints = 100;

        toObjFormat.writeParametricCurve(curve, numOfPoints * 10);

        double step = 1.0 / (numOfPoints - 1);
        Vec3[] points = new Vec3[numOfPoints];
        Vec3[] tangents = new Vec3[numOfPoints];

        for (int i = 0; i < numOfPoints; i++) {
            Vec3 p = curve.point(i*step);
            points[i] = p;
            Vec3 tangent = curve.tangent(i*step, 0.0001);
            tangents[i] = tangent;
            //toObjFormat.writeParametricCurve(new Segment(p, p.add(tangent.mulScalar(4))), 2);
        }

        var vi = tangents[0].perpendicular();
        var vj = tangents[0].cross(vi);

        Frame initialFrame = new Frame(points[0], vi, vj, tangents[0]);
        Frame[] frames = Frame.rotationMinimizingFrames(points, tangents, initialFrame);

        for (Frame f: frames) {
            toObjFormat.writeFrame(f);
        }

        sw.flush();
        String objFormat = sw.toString();
        writeToTempFile("obj-cubic-bezier-", ".obj", objFormat);
    }

    private void writeToTempFile(String prefix, String suffix, String content) throws IOException {
        Path tempFile = Files.createTempFile(prefix, suffix);
        try (PrintWriter out = new PrintWriter(tempFile.toFile(), StandardCharsets.UTF_8)) {
            out.println(content);
            out.flush();
        }
        System.out.println(tempFile);
    }
}