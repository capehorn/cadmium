package capehorn.cadmium.core;

public class Vec4 {

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final int W = 3;

    public final double[] vs;

    public Vec4(double x, double y, double z, double w) {
        this.vs = new double[] {x, y, z, w};
    }

    public static Vec4 of(double x, double y, double z, double w) {
        return new Vec4(x, y, z, w);
    }

    public Vec4 copy() {
        return new Vec4(vs[X], vs[Y], vs[Z], vs[W]);
    }

    public Vec3 toVec3() {
        return Vec3.of(vs[X], vs[Y], vs[Z]);
    }

}
