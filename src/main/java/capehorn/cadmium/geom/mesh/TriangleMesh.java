package capehorn.cadmium.geom.mesh;

import capehorn.cadmium.core.VecBuffer;
import capehorn.cadmium.geom.Triangle;

import java.util.Map;

public class TriangleMesh {
    private final VecBuffer buff;
    private enum Item {
        p1(3),
        p2(3),
        p3(3),
        n(3);

        private final int size;

        Item(int size) {
            this.size = size;
        }
    }

    private TriangleMesh() {
        this.buff = new VecBuffer(new int[]{Item.p1.size, Item.p2.size, Item.p3.size, Item.n.size});
    }

    public void addTriangle(Triangle t) {
        buff.addAll(
                t.getP1().x(), t.getP1().y(), t.getP1().z(),
                t.getP2().x(), t.getP2().y(), t.getP2().z(),
                t.getP3().x(), t.getP3().y(), t.getP3().z(),
                t.getNormal().x(), t.getP1().y(), t.getP1().z()
        );
    }
}
