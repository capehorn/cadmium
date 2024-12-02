package capehorn.cadmium.geom.mesh;

import capehorn.cadmium.core.VecBuffer;
import capehorn.cadmium.geom.Box;
import capehorn.cadmium.geom.Triangle;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TriangleMesh {
    private final VecBuffer buff;
    private enum ItemLayout {
        p1(3),
        p2(3),
        p3(3),
        n(3);

        private final int size;

        ItemLayout(int size) {
            this.size = size;
        }

        public static int[] toArray() {
            return Arrays.stream(ItemLayout.values()).mapToInt(v -> v.size).toArray();
        }
    }

    private Box boundingBox;

    public TriangleMesh() {
        this.buff = new VecBuffer(ItemLayout.toArray());
    }

    public void addTriangles(Triangle... ts) {
        for (var t : ts) {
            buff.put(t.toArray());
        }
        dirty();
    }

    public void addTriangleMesh(TriangleMesh other) {

    }

    public void forEachTriangle(Consumer<Triangle> consumer) {
        buff.setPosition(0);
        int limit = buff.getLimit();
        while (buff.getPosition() < limit) {
            consumer.accept(Triangle.fromArray(buff.get()));
        }
    }

    private void dirty() {
        boundingBox = null;
    }
}
