package capehorn.cadmium.geom;

import capehorn.cadmium.core.AffineTransform;
import capehorn.cadmium.core.Mat4x4;
import capehorn.cadmium.core.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class CubicBezierCurve implements AffineTransform<CubicBezierCurve>, ParametricCurve {
    public final Vec3 p0;
    public final Vec3 p1;
    public final Vec3 p2;
    public final Vec3 p3;
    /**
     * Precomputed transformed control points
     */
    private final Vec3[] coefficients;

    public CubicBezierCurve(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.coefficients = computeCoefficients();
    }

    private Vec3[] computeCoefficients() {
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

    @Override
    public Vec3 point(double t) {
        double tExp2 = t * t;
        double tExp3 = tExp2 * t;
        return coefficients[0].add(coefficients[1].mulScalar(t)).add(coefficients[2].mulScalar(tExp2)).add(coefficients[3].mulScalar(tExp3));
    }

    @Override
    public Vec3 tangent(double t, double delta) {
        double tExp2 = t * t;
        return coefficients[1].add(coefficients[2].mulScalar(2 * t)).add(coefficients[3].mulScalar(3 * tExp2)).normalize();
    }

    public CubicBezierCurve[] splitAt(double t) {
        Vec3 p = point(t);
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

    /**
     * Checking the perpendicular distance of the inner control points from the line
     * defined by the first and last control point. Let's call these distances d1 and d2.
     * If d1 and d2 are less than or equal of the tolerance we consider the curve as being straight enough to approximate
     * it with its control points.
     * (If the inner control points are almost collinear with the P0 P1 line then we approximate the curve with its control points)
     *
     * @param tolerance acceptable tolerance
     * @return a series of points that approximate the cubic Bezier curve fulfilling the tolerance criteria
     */
    public List<Vec3> approximate(double tolerance) {
        List<Vec3> points = new ArrayList<>();
        ArrayDeque<CubicBezierCurve> curves = new ArrayDeque<>();
        curves.add(this);
        while (!curves.isEmpty()) {
            var c = curves.getLast();
            var d1 = c.p1.perpendicularDistanceFrom(c.p0, c.p3);
            var d2 = c.p2.perpendicularDistanceFrom(c.p0, c.p3);
            if (d1 <= tolerance && d2 <= tolerance) {
                points.add(c.p0);
                points.add(c.p1);
                points.add(c.p2);
                points.add(c.p3);
            } else {
                var subCurves = c.splitAt(0.5);
                curves.addLast(subCurves[1]);
                curves.addLast(subCurves[0]);
            }
        }
        return points;
    }

}
