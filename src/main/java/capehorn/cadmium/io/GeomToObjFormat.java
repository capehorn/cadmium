package capehorn.cadmium.io;

import capehorn.cadmium.format.Obj;
import capehorn.cadmium.geom.Frame;
import capehorn.cadmium.geom.ParametricCurve;
import capehorn.cadmium.geom.mesh.TriangleMesh;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class GeomToObjFormat {

    private final Obj.ObjFormatWriter writer;

    public GeomToObjFormat(Writer writer) {
        this.writer = new Obj.ObjFormatWriter(writer);
    }

    public void writeTriangleMesh(TriangleMesh mesh) throws IOException {
        List<int[]> faces = new ArrayList<>();
        mesh.forEachTriangle(t -> {
            faces.add(new int[] {
                    writer.writeVertex(t.p1()),
                    writer.writeVertex(t.p2()),
                    writer.writeVertex(t.p3())
            });
        });
        for(var f: faces) {
            writer.writeFace(f);
        }
    }

    public void writeParametricCurve(ParametricCurve curve, int numOfPoints) throws IOException {
        double step = 1.0 / (numOfPoints - 1);
        var vIndices = new int[numOfPoints];
        for (int i = 0; i < numOfPoints; i++) {
            vIndices[i] = writer.writeVertex(curve.point(i * step));
        }
        writer.writeLine(vIndices);
    }

    public void writeFrame(Frame frame) throws IOException {
        int o = writer.writeVertex(frame.orig());

        int f = writer.writeVertex(frame.orig().add(frame.e1()));
        int s = writer.writeVertex(frame.orig().add(frame.e2()));
        int t = writer.writeVertex(frame.orig().add(frame.e3()));

        writer.writeLine(new int[]{o, f});
        writer.writeLine(new int[]{o, s});
        writer.writeLine(new int[]{o, t});
    }
}
