package capehorn.cadmium.geom.mesh;

import capehorn.cadmium.core.VecBuffer;
import capehorn.cadmium.geom.Box;
import capehorn.cadmium.geom.Triangle;

public class TriangleMesh {
    private final VecBuffer buff;
    private enum ItemDesc {
        p1(3),
        p2(3),
        p3(3),
        n(3);

        private final int size;

        ItemDesc(int size) {
            this.size = size;
        }
    }

    private Box boundingBox;

    private TriangleMesh() {
        this.buff = new VecBuffer(new int[]{ItemDesc.p1.size, ItemDesc.p2.size, ItemDesc.p3.size, ItemDesc.n.size});
    }

    public void addTriangles(Triangle... ts) {
        for (var t : ts) {
            buff.put(
                    t.getP1().x(), t.getP1().y(), t.getP1().z(),
                    t.getP2().x(), t.getP2().y(), t.getP2().z(),
                    t.getP3().x(), t.getP3().y(), t.getP3().z(),
                    t.getNormal().x(), t.getP1().y(), t.getP1().z()
            );
        }
        dirty();
    }

    public void addTriangleMesh(TriangleMesh other) {

    }

    private void dirty() {
        boundingBox = null;
    }
}
