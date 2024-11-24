package capehorn.cadmium.core;

public record Vec4(double x, double y, double z, double w) {

    public static Vec4 of(double x, double y, double z, double w) {
        return new Vec4(x, y, z, w);
    }

    @Override
    public String toString() {
        return "Vec4{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }

    public Vec4 copy() {
        return new Vec4(x, y, z, w);
    }

    public Vec3 toVec3() {
        return Vec3.of(x, y, z);
    }

}
