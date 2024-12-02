package capehorn.cadmium.geom;

import capehorn.cadmium.core.AffineTransform;
import capehorn.cadmium.core.Mat4x4;
import capehorn.cadmium.core.Vec3;

import java.util.Objects;

public record Triangle(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 normal) implements AffineTransform<Triangle> {

    public Triangle(Vec3 p1, Vec3 p2, Vec3 p3) {
        this(p1, p2, p3, computeNormal(p1, p2, p3));
    }

    public static Vec3 computeNormal(Vec3 p1, Vec3 p2, Vec3 p3) {
        return p2.sub(p1).cross(p3.sub(p1)).normalize();
    }

    public double[] toArray() {
        return new double[] {
                p1.x(), p1.y(), p1.z(),
                p2.x(), p2.y(), p2.z(),
                p3.x(), p3.y(), p3.z(),
                normal.x(), normal.y(), normal.z(),
        };
    }

    public static Triangle fromArray(double[] vs) {
        if (vs.length == 9) {
            return new Triangle(
                    Vec3.of(vs[0], vs[1], vs[2]),
                    Vec3.of(vs[3], vs[4], vs[5]),
                    Vec3.of(vs[6], vs[7], vs[8]));
        } else if (vs.length == 12) {
            return new Triangle(
                    Vec3.of(vs[0], vs[1], vs[2]),
                    Vec3.of(vs[3], vs[4], vs[5]),
                    Vec3.of(vs[6], vs[7], vs[8]),
                    Vec3.of(vs[9], vs[10], vs[11]));
        }
        throw new IllegalArgumentException("The input length should be 9 (3 points) or 12 (3 points + normal)");
    }

    public double area() {
        return p2.sub(p1).cross(p3.sub(p1)).length() / 2;
    }

    public Box boundingBox() {
        var min = p1.min(p2).min(p3);
        var max = p1.max(p2).max(p3);
        return new Box(min, max);
    }

    public Triangle transform(Mat4x4 trf) {
        return new Triangle(trf.mulPosition(p1), trf.mulPosition(p2), trf.mulPosition(p3));
    }

    public Triangle reverseWinding() {
        return new Triangle(p3, p2, p1);
    }

    public Vec3 centroid() {
        return Vec3.of(
                (p1.x() + p2.x() + p3.x()) / 3,
                (p1.y() + p2.y() + p3.y()) / 3,
                (p1.z() + p2.z() + p3.z()) / 3);
    }

    public boolean isDegenerate() {
        return p1.equals(p2) || p1.equals(p3) || p2.equals(p3)
                || p1.isDegenerate() || p2.isDegenerate() || p3.isDegenerate();
    }

    @Override
    public String toString() {
        return "Triangle{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", p3=" + p3 +
                '}';
    }
}
