package capehorn.cadmium.geom;

import capehorn.cadmium.core.AffineTransform;
import capehorn.cadmium.core.Mat4x4;
import capehorn.cadmium.core.Vec3;

public record Box(Vec3 min, Vec3 max) implements AffineTransform<Box> {
    public static final Box EMPTY = new Box(Vec3.of(0, 0, 0), Vec3.of(0, 0, 0));

    @Override
    public String toString() {
        return "Box{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }

    public double volume() {
        var s = size();
        return s.x() * s.y() * s.z();
    }

    public Vec3 anchor(Vec3 anchor) {
        return min.add(size().mul(anchor));
    }

    public Vec3 center() {
        return anchor(Vec3.of(0.5, 0.5, 0.5));
    }

    public Vec3 size() {
        return max.sub(min);
    }

    public Box extend(Box b) {
        return this.equals(EMPTY) ? b : new Box(min.min(b.min), max.max(b.max));
    }

    public Box offset(double x) {
        return new Box(min.subScalar(x), max.addScalar(x));
    }

    public Box translate(Vec3 v) {
        return new Box(min.add(v), max.add(v));
    }

    public boolean contains(Vec3 v) {
        return min.x() <= v.x() && max.x() >= v.x() &&
                min.y() <= v.y() && max.y() >= v.y() &&
                min.z() <= v.z() && max.z() >= v.z();
    }

    public boolean containsBox(Box b) {
        return min.x() <= b.min.x() && max.x() >= b.max.x() &&
                min.y() <= b.min.y() && max.y() >= b.max.y() &&
                min.z() <= b.min.z() && max.z() >= b.max.z();
    }

    public boolean intersects(Box b) {
        return !(min.x() > b.max.x() || max.x() < b.min.x() || min.y() > b.max.y() ||
                max.y() < b.min.y() || min.z() > b.max.z() || max.z() < b.min.z());
    }

    public Box intersection(Box b) {
        if (!intersects(b)) {
            return EMPTY;
        }
        var minLoc = min.max(b.min);
        var maxLoc = max.min(b.max);
        var temp = minLoc;
        minLoc = minLoc.min(max);
        maxLoc = temp.max(max);
        return new Box(minLoc, maxLoc);
    }

    public Box transform(Mat4x4 mat) {
        return mat.mulBox(this);
    }
}
