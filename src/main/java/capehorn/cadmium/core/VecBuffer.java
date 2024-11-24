package capehorn.cadmium.core;

import java.util.Arrays;
import java.util.function.Function;

public class VecBuffer {

    private final int[] itemPattern;
    private final int itemSize;
    private int numOfItems;
    private double[] vs;
    private int capacity;
    private int length;

    public VecBuffer(int[] itemPattern) {
        this(itemPattern, 32);
    }

    public VecBuffer(int[] itemPattern, int numOfItems) {
        this.itemPattern = itemPattern;
        this.numOfItems = numOfItems;
        this.itemSize = Arrays.stream(this.itemPattern).sum();
        this.capacity = this.numOfItems * this.itemSize;
        this.length = 0;
    }

    public static VecBuffer of(Vec3... elements) {
        VecBuffer buffer = new VecBuffer(new int[]{3}, elements.length * 3);
        for (var vec : elements) {
            buffer.addAll(vec.x(), vec.y(), vec.z());
        }
        return buffer;
    }

    public static VecBuffer of(Vec4... elements) {
        VecBuffer buffer = new VecBuffer(new int[]{4}, elements.length * 4);
        for (var vec : elements) {
            buffer.addAll(vec.x(), vec.y(), vec.z(), vec.w());
        }
        return buffer;
    }

    public double[] get(int lineIdx) {
        // TODO check line index
        int from = lineIdx * itemSize;
        return Arrays.copyOfRange(vs, from, from + itemSize);
    }

    public double[] getInto(int lineIdx, double[] target) {
        // TODO check line index
        int from = lineIdx * itemSize;
        return Arrays.copyOfRange(target, from, from + itemSize);
    }

    public void addAll(double... elements) {
        // TODO check elements length
        // TODO check capacity
    }

    public void recomputeLine(Function<double[], double[]> fn, int offset) {
        for (int i = 0; i < length; i++) {
            var startIdx = i * itemSize;
            var updatedElements = fn.apply(get(startIdx));
            var lengthOfUpdatedElements = updatedElements.length;
            if (itemSize == lengthOfUpdatedElements + offset) {
                System.arraycopy(updatedElements, 0, vs, startIdx + offset, lengthOfUpdatedElements);
            }
        }
    }
    
}
