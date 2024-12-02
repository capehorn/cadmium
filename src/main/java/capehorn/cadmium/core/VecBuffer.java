package capehorn.cadmium.core;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *      0 <= position <= limit <= capacity
 */
public class VecBuffer {

    private final int[] itemLayout;
    private final int itemSize;

    private double[] storage;
    /**
     * Current position
     */
    private int position;
    private int limit;
    /**
     * Max. number of items this buffer can hold
     */
    private int capacity;

    public VecBuffer(int[] itemLayout) {
        this(itemLayout, 32);
    }

    public VecBuffer(int[] itemLayout, int numOfItems) {
        this.itemLayout = itemLayout;
        this.itemSize = Arrays.stream(this.itemLayout).sum();
        this.storage = new double[numOfItems * this.itemSize];
        this.capacity = numOfItems;
        this.position = 0;
    }

    private VecBuffer(int[] itemLayout, double[] src) {
        this.itemLayout = itemLayout;
        this.itemSize = Arrays.stream(this.itemLayout).sum();
        this.storage = src;
        this.capacity = src.length / itemSize;
        this.position = this.capacity;
        this.limit = this.capacity;
    }

    public static VecBuffer of(Vec3... elements) {
        int dim = 3;
        var length = elements.length;
        var data = new double[length * dim];
        for (int i = 0; i < length; i++) {
            System.arraycopy(elements[i].toArray(), 0, data, i * dim, dim);
        }
        return new VecBuffer(new int[]{dim}, data);
    }

    public static VecBuffer of(Vec4... elements) {
        int dim = 4;
        var length = elements.length;
        var data = new double[length * dim];
        for (int i = 0; i < length; i++) {
            System.arraycopy(elements[i].toArray(), 0, data, i * dim, dim);
        }
        return new VecBuffer(new int[]{dim}, data);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCapacity() {
        return capacity;
    }

    public double[] get() {
        if (limit <= position) {
            throw new IllegalStateException("Can't read item beyond the limit ");
        }
        double[] item = Arrays.copyOfRange(storage, position * itemSize, (position + 1) * itemSize);
        position += 1;
        return item;
    }

    public double[] get(int itemIdx) {
        if (limit <= itemIdx) {
            throw new IllegalStateException("Can't read item beyond the limit ");
        }
        int from = itemIdx * itemSize;
        return Arrays.copyOfRange(storage, from, from + itemSize);
    }

    public void get(int itemIdx, double[] dst) {
        if (dst.length != itemSize) {
            throw new IllegalArgumentException("Length of dst must be " + itemSize);
        }
        if (limit <= itemIdx) {
            throw new IllegalStateException("Can't read item beyond the limit ");
        }
        System.arraycopy(storage, itemIdx * itemSize, dst,0, itemSize);
    }

    public void put(double... src) {
        int srcLength = src.length;
        if (1 < itemSize && srcLength % itemSize != 0) {
            throw new IllegalArgumentException("Length of src must be multiple of " + itemSize);
        }
        int numOfItems = srcLength / itemSize;
        if ((capacity - position) < numOfItems) {
            grow();
        }
        System.arraycopy(src, 0, this.storage, position * itemSize, srcLength);
        position += numOfItems;
        limit = Math.max(limit, position);
    }

    public void recompute(int itemIdx, Function<double[], double[]> fn) {
        System.arraycopy(fn.apply(get(itemIdx)), 0, storage, itemIdx * itemSize, itemSize);
    }

    public void recompute(BiFunction<Integer, double[], double[]> fn) {
        for (int i = 0; i < limit; i++) {
            System.arraycopy(fn.apply(i, get(i)), 0, storage, i * itemSize, itemSize);
        }
    }

    private void grow() {
        // right now just double the size, later can be driven by different strategies
        int newCapacity = capacity * 2;
        double[] newStorage = new double[newCapacity * itemSize];
        System.arraycopy(storage, 0, newStorage, 0, capacity * itemSize);
        this.storage = newStorage;
        this.capacity = newCapacity;
    }
    
}
