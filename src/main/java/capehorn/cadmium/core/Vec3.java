package capehorn.cadmium.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Vec3(double x, double y, double z) {

    public static Vec3 of(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    @Override
    public String toString() {
        return "Vec3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public Vec3 copy() {
        return new Vec3(x, y, z);
    }

    public Vec4 toVec4(double w) {
        return Vec4.of(x, y, z, w);
    }

    public Vec3 abs() {
        return new Vec3(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public Vec3 add(Vec3 v) {
        return new Vec3(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 sub(Vec3 v) {
        return new Vec3(x - v.x, y - v.y, z - v.z);
    }

    public Vec3 mul(Vec3 v) {
        return new Vec3(x * v.x, y * v.y, z * v.z);
    }

    public Vec3 div(Vec3 v) {
        return new Vec3(x / v.x, y / v.y, z / v.z);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3 negate() {
        return new Vec3(-x, -y, -z);
    }

    public Vec3 normalize() {
        double r = 1 / Math.sqrt(x * x + y * y + z * z);
        return new Vec3(x * r, y * r, z * r);
    }

    public double dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    public Vec3 lerp(Vec3 v, double t) {
        return add(v.sub(this).mulScalar(t));
    }

    public Vec3 lerpDistance(Vec3 v, double t) {
        return add(v.sub(this).normalize().mulScalar(t));
    }

    public Vec3 mod(Vec3 v) {
        return new Vec3(
                x - v.x * Math.floor(x / v.x),
                y - v.y * Math.floor(y / v.y),
                z - v.z * Math.floor(z / v.z)
        );
    }

    public Vec3 addScalar(double s) {
        return new Vec3(x + s, y + s, z + s);
    }

    public Vec3 subScalar(double s) {
        return new Vec3(x - s, y - s, z - s);
    }

    public Vec3 mulScalar(double s) {
        return new Vec3(x * s, y * s, z * s);
    }

    public Vec3 divScalar(double s) {
        return new Vec3(x / s, y / s, z / s);
    }

    public Vec3 min(Vec3 v) {
        return new Vec3(Math.min(x, v.x), Math.min(y, v.y), Math.min(z, v.z));
    }

    public Vec3 max(Vec3 v) {
        return new Vec3(Math.max(x, v.x), Math.max(y, v.y), Math.max(z, v.z));
    }

    public Vec3 floor() {
        return new Vec3(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    public Vec3 ceil() {
        return new Vec3(Math.ceil(x), Math.ceil(y), Math.ceil(z));
    }

    public Vec3 round() {
        return roundPlaces(0);
    }

    public Vec3 roundPlaces(int n) {
        return new Vec3(
                BigDecimal.valueOf(x).setScale(n, RoundingMode.HALF_UP).doubleValue(),
                BigDecimal.valueOf(y).setScale(n, RoundingMode.HALF_UP).doubleValue(),
                BigDecimal.valueOf(z).setScale(n, RoundingMode.HALF_UP).doubleValue()
        );
    }

    public double minComponent() {
        return Math.min(Math.min(x, y), z);
    }

    public double maxComponent() {
        return Math.max(Math.max(x, y), z);
    }

    public Vec3 reflect(Vec3 v) {
        return sub(v.mulScalar(2 * v.dot(this)));
    }

    public Vec3 perpendicular() {
        if (x == 0 && y == 0) {
            if (z == 0) {
                return new Vec3(0, 0, 0);
            }
            return new Vec3(0, 1, 0);
        }
        return new Vec3(-y, x, 0).normalize();
    }

    public double distance(Vec3 v) {
        return this.sub(v).length();
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double distanceSquared(Vec3 v) {
        return this.sub(v).lengthSquared();
    }


    // distance between p and m (point segment distance)
    //
    //	               x v
    //	p x           /
    //	             /
    //	            x m
    //	           /
    //	          /
    //	         x w
    public double segmentDistance(Vec3 v, Vec3 w) {
        double d2 = v.distanceSquared(w);
        if (d2 == 0) {
            return distance(v);
        }
        double t = sub(v).dot(w.sub(v)) / d2;
        if (t < 0) {
            return distance(v);
        }
        if (t > 1) {
            return distance(w);
        }
        return v.add(w.sub(v).mulScalar(t)).distance(this);
    }

    public boolean isDegenerate() {
        return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)
                || Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z);
    }

}
