package capehorn.cadmium.io;

import capehorn.cadmium.core.Vec3;
import capehorn.cadmium.geom.Triangle;
import capehorn.cadmium.geom.mesh.TriangleMesh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

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
        System.out.println(objFormat);
    }
}