package capehorn.cadmium.io;

import capehorn.cadmium.format.Obj;
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
}
