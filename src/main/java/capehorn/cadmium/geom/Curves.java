package capehorn.cadmium.geom;

import capehorn.cadmium.core.Vec3;

import java.util.function.Function;

public final class Curves {

    public static Circle circle(Vec3 center, double r) {
        return new Circle(center, r);
    }

    public static Ellipse ellipse(Vec3 center, double r1, double r2) {
        return new Ellipse(center, r1, r2);
    }

    public static ParametricCurve custom(
            Function<Double, Double> fnX,
            Function<Double, Double> fnY,
            Function<Double, Double> fnZ) {
        return new ParametricCurve() {
            @Override
            public Vec3 point(double t) {
                return Vec3.of(fnX.apply(t), fnY.apply(t), fnZ.apply(t));
            }

            @Override
            public Vec3 tangent(double t, double delta) {
                var p1 = point(Math.max(0, t - delta));
                var p2 = point(Math.min(1, t + delta));
                return p2.sub(p1).normalize();
            }
        };
    }

    public record Circle(Vec3 center, double r) implements ParametricCurve {

        @Override
        public Vec3 point(double t) {
            return Vec3.of(
                    center.x() + r * Math.cos(Math.PI * t),
                    center.y() + r * Math.sin(Math.PI * t),
                    0
            );
        }

        @Override
        public Vec3 tangent(double t, double delta) {
            return null;
        }
    }

    public record Ellipse(Vec3 center, double r1, double r2) implements ParametricCurve {

        @Override
        public Vec3 point(double t) {
            return Vec3.of(
                    center.x() + r1 * Math.cos(Math.PI * t),
                    center.y() + r2 * Math.sin(Math.PI * t),
                    0
            );
        }

        @Override
        public Vec3 tangent(double t, double delta) {
            return null;
        }
    }

}
