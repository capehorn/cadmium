package capehorn.cadmium.geom;

import capehorn.cadmium.core.Vec3;

public record Frame(Vec3 orig, Vec3 e1, Vec3 e2, Vec3 e3) {

    public static final Frame Standard = new Frame(
                Vec3.of(0, 0, 0),
                Vec3.of(1, 0, 0),
                Vec3.of(0, 1, 0),
                Vec3.of(0, 0, 1));

    /**
     * <start href="https://www.microsoft.com/en-us/research/wp-content/uploads/2016/12/Computation-of-rotation-minimizing-frames.pdf">RMF computation</start>
     * @param points curve points
     * @param tangents tangents at the given points
     * @param initialFrame initial frame for RMF
     * @return RMF at the given points
     */
    public static Frame[] rotationMinimizingFrames(Vec3[] points, Vec3[] tangents, Frame initialFrame) {
        if (! initialFrame.e3().equals(tangents[0])) {
            throw new IllegalArgumentException(
                    "For calculating RMF, the initial frame's e3 component must be the first tangent vector");
        }
        Frame[] frames = new Frame[points.length];
        frames[0] = initialFrame;

        for (int i = 0; i < tangents.length - 1; i++) {
            Vec3 tg = tangents[i];
            Vec3 tgNext = tangents[i + 1];
            Vec3 r = frames[i].e1();
            Vec3 v1 = points[i + 1].sub(points[i]);
            double c1 = v1.dot(v1);
            Vec3 rli = r.sub( v1.mulScalar(v1.dot(r)).mulScalar(2/c1) );
            Vec3 tli = tg.sub( v1.mulScalar(v1.dot(tg)).mulScalar(2/c1) );
            Vec3 v2 = tgNext.sub(tli);
            double c2 = v2.dot(v2);
            Vec3 rNext = rli.sub( v2.mulScalar(v2.dot(rli)).mulScalar(2/c2) );
            Vec3 sNext = tgNext.cross(rNext);

            frames[i + 1] = new Frame(points[i + 1], rNext, sNext, tgNext);
        }
        return frames;
    }

}
