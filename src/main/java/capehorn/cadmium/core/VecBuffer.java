package capehorn.cadmium.core;

import java.util.Arrays;
import java.util.function.Function;

public class VecBuffer {

    private final int[] linePattern;
    private final int lineSize;
    private int numOfLines;
    private double[] vs;
    private int capacity;
    private int length;

    public VecBuffer(int[] linePattern) {
        this(linePattern, 32);
    }

    public VecBuffer(int[] linePattern, int numOfLines) {
        this.linePattern = linePattern;
        this.numOfLines = numOfLines;
        this.lineSize = Arrays.stream(this.linePattern).sum();
        this.capacity = this.numOfLines * this.lineSize;
        this.length = 0;
    }

    public static VecBuffer of(Vec3... elements) {
        VecBuffer buffer = new VecBuffer(new int[]{3}, elements.length * 3);
        for (var vec : elements) {
            buffer.addAll(vec.vs);
        }
        return buffer;
    }

    public static VecBuffer of(Vec4... elements) {
        VecBuffer buffer = new VecBuffer(new int[]{4}, elements.length * 4);
        for (var vec : elements) {
            buffer.addAll(vec.vs);
        }
        return buffer;
    }

    public double[] get(int lineIdx) {
        // TODO check line index
        int from = lineIdx * lineSize;
        return Arrays.copyOfRange(vs, from, from + lineSize);
    }

    public double[] getInto(int lineIdx, double[] target) {
        // TODO check line index
        int from = lineIdx * lineSize;
        return Arrays.copyOfRange(target, from, from + lineSize);
    }

    public void addAll(double... elements) {
        // TODO check elements length
        // TODO check capacity
    }

    public void recomputeLine(Function<double[], double[]> fn, int offset) {
        for (int i = 0; i < length; i++) {
            var startIdx = i * lineSize;
            var updatedElements = fn.apply(get(startIdx));
            var lengthOfUpdatedElements = updatedElements.length;
            if (lineSize == lengthOfUpdatedElements + offset) {
                System.arraycopy(updatedElements, 0, vs, startIdx + offset, lengthOfUpdatedElements);
            }
        }
    }
    
}
