package capehorn.cadmium.geom;

import capehorn.cadmium.core.AffineTransform;
import capehorn.cadmium.core.Mat4x4;
import capehorn.cadmium.core.Vec3;

public class CubicBezierCurve implements AffineTransform<CubicBezierCurve> {
    public final Vec3 p0;
    public final Vec3 p1;
    public final Vec3 p2;
    public final Vec3 p3;
    /**
     * Precomputed transformed control points
     */
    private final Vec3[] tcps;

    public CubicBezierCurve(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.tcps = computeTransformedControlPoints();
    }

    private Vec3[] computeTransformedControlPoints() {
        return new Vec3[]{
            p0,
            p0.mulScalar(-3).add(p1.mulScalar(3)),
            p0.mulScalar(3).add(p1.mulScalar(-6)).add(p2.mulScalar(3)),
            p0.mulScalar(-1).add(p1.mulScalar(3)).add(p2.mulScalar(-3)).add(p3)
        };
    }

    @Override
    public CubicBezierCurve transform(Mat4x4 trf) {
        return new CubicBezierCurve(
                trf.mulPosition(p0),
                trf.mulPosition(p1),
                trf.mulPosition(p2),
                trf.mulPosition(p3));
    }

    public Vec3 pointAt(double t) {
        double tExp2 = t * t;
        double tExp3 = tExp2 * t;
        return tcps[0].add(tcps[1].mulScalar(t)).add(tcps[2].mulScalar(tExp2)).add(tcps[3].mulScalar(tExp3));
    }

    public CubicBezierCurve[] splitAt(double t) {
        Vec3 p = pointAt(t);
        Vec3 m = p1.lerp(p2, t);

        Vec3 q1 = p0.lerp(p1, t);
        Vec3 q2 = q1.lerp(m, t);

        Vec3 r2 = p3.lerp(p2, 1-t);
        Vec3 r1 = r2.lerp(m, 1-t);

        return new CubicBezierCurve[]{
                new CubicBezierCurve(p0, q1, q2, p),
                new CubicBezierCurve(p, r1, r2, p3)
        };
    }

}
