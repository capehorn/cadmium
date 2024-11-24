package capehorn.cadmium.geom;

import capehorn.cadmium.core.AffineTransform;
import capehorn.cadmium.core.Mat4x4;
import capehorn.cadmium.core.Vec3;

import java.util.Objects;

public class Triangle implements AffineTransform<Triangle> {
    private final Vec3 p1;
    private final Vec3 p2;
    private final Vec3 p3;
    private final Vec3 normal;

    public Triangle(Vec3 p1, Vec3 p2, Vec3 p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.normal = p2.sub(p1).cross(p3.sub(p1)).normalize();
    }

    public Vec3 getP1() {
        return p1;
    }

    public Vec3 getP2() {
        return p2;
    }

    public Vec3 getP3() {
        return p3;
    }

    public Vec3 getNormal() {
        return normal;
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Triangle triangle = (Triangle) o;
        return Objects.equals(p1, triangle.p1) && Objects.equals(p2, triangle.p2) && Objects.equals(p3, triangle.p3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p1, p2, p3);
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
