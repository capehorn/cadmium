package capehorn.cadmium.geom;

import capehorn.cadmium.core.AffineTransform;
import capehorn.cadmium.core.Mat4x4;
import capehorn.cadmium.core.Vec3;

public record Segment(Vec3 p1, Vec3 p2) implements AffineTransform<Segment>, ParametricCurve {

    public Box boundingBox() {
        return new Box(p1.min(p2), p1.max(p2));
    }

    public Segment transform(Mat4x4 trf) {
        return new Segment(trf.mulPosition(p1), trf.mulPosition(p2));
    }

    @Override
    public Vec3 point(double t) {
        return p1.lerp(p2, t);
    }

    @Override
    public Vec3 tangent(double t, double delta) {
        return null;
    }

}
