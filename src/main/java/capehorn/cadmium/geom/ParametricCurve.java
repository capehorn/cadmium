package capehorn.cadmium.geom;

import capehorn.cadmium.core.Vec3;

public interface ParametricCurve {

    Vec3 point(double t);

    Vec3 tangent(double t, double delta);

}
