package capehorn.cadmium.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Vec3 {
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    public final double[] vs;

    private Vec3(double x, double y, double z) {
        this.vs = new double[] {x, y, z};
    }

    public static Vec3 of(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public Vec3 copy() {
        return new Vec3(vs[X], vs[Y], vs[Z]);
    }

    public Vec4 toVec4(double w) {
        return Vec4.of(vs[X], vs[Y], vs[Z], w);
    }

    public Vec3 abs() {
        return new Vec3(Math.abs(vs[X]), Math.abs(vs[Y]), Math.abs(vs[Z]));
    }

    public Vec3 add(Vec3 v) {
        var os = v.vs;
        return new Vec3(vs[X]+os[X], vs[Y]+os[Y], vs[Z]+os[Z]);
    }

    public Vec3 sub(Vec3 v) {
        var os = v.vs;
        return new Vec3(vs[X]-os[X], vs[Y]-os[Y], vs[Z]-os[Z]);
    }

    public Vec3 mul(Vec3 v) {
        var os = v.vs;
        return new Vec3(vs[X]*os[X], vs[Y]*os[Y], vs[Z]*os[Z]);
    }

    public Vec3 div(Vec3 v) {
        var os = v.vs;
        return new Vec3(vs[X]/os[X], vs[Y]/os[Y], vs[Z]/os[Z]);
    }

    public double length() {
        return Math.sqrt(vs[X]*vs[X] + vs[Y]*vs[Y] + vs[Z]*vs[Z]);
    }

    public Vec3 negate(Vec3 v) {
        return new Vec3(-vs[X], -vs[Y], -vs[Z]);
    }

    public Vec3 normalize() {
        double r = 1 / Math.sqrt(vs[X]*vs[X] + vs[Y]*vs[Y] + vs[Z]*vs[Z]);
        return new Vec3(vs[X] * r, vs[Y] * r, vs[Z] * r);
    }

    public double dot(Vec3 v) {
        var os = v.vs;
        return vs[X]*os[X] + vs[Y]*os[Y] + vs[Z]*os[Z];
    }

    public Vec3 cross(Vec3 v) {
        var os = v.vs;
        return new Vec3(
                vs[Y]*os[Z] - vs[Z]*os[Y],
                vs[Z]*os[X] - vs[X]*os[Z],
                vs[X]*os[Y] - vs[Y]*os[X]
        );
    }

    public Vec3 lerp(Vec3 v, double t) {
        return add(v.sub(this).mulScalar(t));
    }

    public Vec3 lerpDistance(Vec3 v, double t) {
        return add(v.sub(this).normalize().mulScalar(t));
    }


    public Vec3 mod(Vec3 v) {
        // as implemented in GLSL
        var os = v.vs;
        return new Vec3(
                vs[X] - os[X]*Math.floor(vs[X]/os[X]),
                vs[Y] - os[Y]*Math.floor(vs[Y]/os[Y]),
                vs[Z] - os[Z]*Math.floor(vs[Z]/os[Z])
        );
    }

    public Vec3 addScalar(double s) {
        return new Vec3(vs[X]+s, vs[Y]+s, vs[Z]+s);
    }

    public Vec3 subScalar(double s) {
        return new Vec3(vs[X]-s, vs[Y]-s, vs[Z]-s);
    }

    public Vec3 mulScalar(double s) {
        return new Vec3(vs[X]*s, vs[Y]*s, vs[Z]*s);
    }

    public Vec3 divScalar(double s) {
        return new Vec3(vs[X]/s, vs[Y]/s, vs[Z]/s);
    }

    public Vec3 min(Vec3 v) {
        var os = v.vs;
        return new Vec3(Math.min(vs[X], os[X]), Math.min(vs[Y], os[Y]), Math.min(vs[Z], os[Z]));
    }

    public Vec3 max(Vec3 v) {
        var os = v.vs;
        return new Vec3(Math.max(vs[X], os[X]), Math.max(vs[Y], os[Y]), Math.max(vs[Z], os[Z]));
    }

    public Vec3 floor() {
        return new Vec3(Math.floor(vs[X]), Math.floor(vs[Y]), Math.floor(vs[Z]));
    }

    public Vec3 ceil() {
        return new Vec3(Math.ceil(vs[X]), Math.ceil(vs[Y]), Math.ceil(vs[Z]));
    }

    public Vec3 round() {
        return roundPlaces(0);
    }

    public Vec3 roundPlaces(int n) {
        return new Vec3(
                BigDecimal.valueOf(vs[X]).setScale(n, RoundingMode.HALF_UP).doubleValue(),
                BigDecimal.valueOf(vs[Y]).setScale(n, RoundingMode.HALF_UP).doubleValue(),
                BigDecimal.valueOf(vs[Z]).setScale(n, RoundingMode.HALF_UP).doubleValue()
        );
    }

    public double minComponent() {
        return Math.min(Math.min(vs[X], vs[Y]), vs[Z]);
    }

    public double maxComponent() {
        return Math.max(Math.max(vs[X], vs[Y]), vs[Z]);
    }

    public Vec3 reflect(Vec3 v) {
        return sub(v.mulScalar(2 * v.dot(this)));
    }

    public Vec3 perpendicular() {
        if (vs[X] == 0 && vs[Y] == 0) {
            if (vs[Z] == 0) {
                return new Vec3(0, 0, 0);
            }
            return new Vec3(0, 1, 0);
        }
        return new Vec3(-vs[Y], vs[X], 0).normalize();
    }

    public double distance(Vec3 v) {
        return this.sub(v).length();
    }

    public double lengthSquared() {
        return vs[X]*vs[X] + vs[Y]*vs[Y] + vs[Z]*vs[Z];
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


}
