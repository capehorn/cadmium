package capehorn.cadmium.geom;

import capehorn.cadmium.core.AffineTransform;
import capehorn.cadmium.core.Mat4x4;
import capehorn.cadmium.core.Vec3;

public record Segment(Vec3 p1, Vec3 p2) implements AffineTransform<Segment> {

    public Box boundingBox() {
        return new Box(p1.min(p2), p1.max(p2));
    }

    public Segment transform(Mat4x4 trf) {
        return new Segment(trf.mulPosition(p1), trf.mulPosition(p2));
    }
}
