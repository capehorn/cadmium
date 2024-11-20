package capehorn.cadmium.core;

import capehorn.cadmium.CadmiumRuntimeException;

import java.util.Arrays;
import java.util.Objects;

import static java.lang.String.format;

public class Mat4x4 {
    public static final Mat4x4 Identity = new Mat4x4(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1);


    public final double[] vs;

    public Mat4x4(double... vs) {
        if (vs.length != 16) {
            throw new CadmiumRuntimeException("Invalid number of elements for matrix 4x4");
        }
        this.vs = vs.clone();
    }

    public static Mat4x4 translation(double x, double y, double z) {
        return new Mat4x4(
            1, 0, 0, x,
            0, 1, 0, y,
            0, 0, 1, z,
            0, 0, 0, 1
        );
    }

    public static Mat4x4 scaling(double x, double y, double z) {
        return new Mat4x4(
                x, 0, 0, 0,
                0, y, 0, 0,
                0, 0, z, 0,
                0, 0, 0, 1
        );
    }

//    public static Mat4x4 rotation(Vec v, double angle) {
//        v = v.Normalize()
//        s := math.Sin(a)
//        c := math.Cos(a)
//        m := 1 - c
//        return Matrix{
//            m*v.X*v.X + c, m*v.X*v.Y + v.Z*s, m*v.Z*v.X - v.Y*s, 0,
//                    m*v.X*v.Y - v.Z*s, m*v.Y*v.Y + c, m*v.Y*v.Z + v.X*s, 0,
//                    m*v.Z*v.X + v.Y*s, m*v.Y*v.Z - v.X*s, m*v.Z*v.Z + c, 0,
//                    0, 0, 0, 1}
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mat4x4 mat4x4 = (Mat4x4) o;
        return Objects.deepEquals(vs, mat4x4.vs);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vs);
    }

    @Override
    public String toString() {
        return format("\n| %s %s %s %s \n| %s %s %s %s \n| %s %s %s %s \n| %s %s %s %s \n"
                , vs[X00], vs[X01], vs[X02], vs[X03]
                , vs[X10], vs[X11], vs[X12], vs[X13]
                , vs[X20], vs[X21], vs[X22], vs[X23]
                , vs[X30], vs[X31], vs[X32], vs[X33]);
    }

    public Mat4x4 translate(double x, double y, double z) {
        return new Mat4x4(
                1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1
        ).mul(this);
    }

    public Mat4x4 transpose() {
        return new Mat4x4(
                vs[X00], vs[X10], vs[X20], vs[X30],
                vs[X01], vs[X11], vs[X21], vs[X31],
                vs[X02], vs[X12], vs[X22], vs[X32],
                vs[X03], vs[X13], vs[X23], vs[X33]
        );
    }

    public Mat4x4 mul(Mat4x4 m) {
        double[] mvs = m.vs;
        return new Mat4x4(
                vs[X00]*mvs[X00] + vs[X01]*mvs[X10] + vs[X02]*mvs[X20] + vs[X03]*mvs[X30],
                vs[X00]*mvs[X01] + vs[X01]*mvs[X11] + vs[X02]*mvs[X21] + vs[X03]*mvs[X31],
                vs[X00]*mvs[X02] + vs[X01]*mvs[X12] + vs[X02]*mvs[X22] + vs[X03]*mvs[X32],
                vs[X00]*mvs[X03] + vs[X01]*mvs[X13] + vs[X02]*mvs[X23] + vs[X03]*mvs[X33],

                vs[X10]*mvs[X00] + vs[X11]*mvs[X10] + vs[X12]*mvs[X20] + vs[X13]*mvs[X30],
                vs[X10]*mvs[X01] + vs[X11]*mvs[X11] + vs[X12]*mvs[X21] + vs[X13]*mvs[X31],
                vs[X10]*mvs[X02] + vs[X11]*mvs[X12] + vs[X12]*mvs[X22] + vs[X13]*mvs[X32],
                vs[X10]*mvs[X03] + vs[X11]*mvs[X13] + vs[X12]*mvs[X23] + vs[X13]*mvs[X33],

                vs[X20]*mvs[X00] + vs[X21]*mvs[X10] + vs[X22]*mvs[X20] + vs[X23]*mvs[X30],
                vs[X20]*mvs[X01] + vs[X21]*mvs[X11] + vs[X22]*mvs[X21] + vs[X23]*mvs[X31],
                vs[X20]*mvs[X02] + vs[X21]*mvs[X12] + vs[X22]*mvs[X22] + vs[X23]*mvs[X32],
                vs[X20]*mvs[X03] + vs[X21]*mvs[X13] + vs[X22]*mvs[X23] + vs[X23]*mvs[X33],

                vs[X30]*mvs[X00] + vs[X31]*mvs[X10] + vs[X32]*mvs[X20] + vs[X33]*mvs[X30],
                vs[X30]*mvs[X01] + vs[X31]*mvs[X11] + vs[X32]*mvs[X21] + vs[X33]*mvs[X31],
                vs[X30]*mvs[X02] + vs[X31]*mvs[X12] + vs[X32]*mvs[X22] + vs[X33]*mvs[X32],
                vs[X30]*mvs[X03] + vs[X31]*mvs[X13] + vs[X32]*mvs[X23] + vs[X33]*mvs[X33]
        );
    }

    double determinant() {
        return (vs[X00]*vs[X11]*vs[X22]*vs[X33] - vs[X00]*vs[X11]*vs[X23]*vs[X32] +
                vs[X00]*vs[X12]*vs[X23]*vs[X31] - vs[X00]*vs[X12]*vs[X21]*vs[X33] +
                vs[X00]*vs[X13]*vs[X21]*vs[X32] - vs[X00]*vs[X13]*vs[X22]*vs[X31] -
                vs[X01]*vs[X12]*vs[X23]*vs[X30] + vs[X01]*vs[X12]*vs[X20]*vs[X33] -
                vs[X01]*vs[X13]*vs[X20]*vs[X32] + vs[X01]*vs[X13]*vs[X22]*vs[X30] -
                vs[X01]*vs[X10]*vs[X22]*vs[X33] + vs[X01]*vs[X10]*vs[X23]*vs[X32] +
                vs[X02]*vs[X13]*vs[X20]*vs[X31] - vs[X02]*vs[X13]*vs[X21]*vs[X30] +
                vs[X02]*vs[X10]*vs[X21]*vs[X33] - vs[X02]*vs[X10]*vs[X23]*vs[X31] +
                vs[X02]*vs[X11]*vs[X23]*vs[X30] - vs[X02]*vs[X11]*vs[X20]*vs[X33] -
                vs[X03]*vs[X10]*vs[X21]*vs[X32] + vs[X03]*vs[X10]*vs[X22]*vs[X31] -
                vs[X03]*vs[X11]*vs[X22]*vs[X30] + vs[X03]*vs[X11]*vs[X20]*vs[X32] -
                vs[X03]*vs[X12]*vs[X20]*vs[X31] + vs[X03]*vs[X12]*vs[X21]*vs[X30]
        );
    }

    public Mat4x4 inverse() {
        double d = this.determinant();
        return new Mat4x4(
            (vs[X12]*vs[X23]*vs[X31] - vs[X13]*vs[X22]*vs[X31] + vs[X13]*vs[X21]*vs[X32] - vs[X11]*vs[X23]*vs[X32] - vs[X12]*vs[X21]*vs[X33] + vs[X11]*vs[X22]*vs[X33]) / d,
            (vs[X03]*vs[X22]*vs[X31] - vs[X02]*vs[X23]*vs[X31] - vs[X03]*vs[X21]*vs[X32] + vs[X01]*vs[X23]*vs[X32] + vs[X02]*vs[X21]*vs[X33] - vs[X01]*vs[X22]*vs[X33]) / d,
            (vs[X02]*vs[X13]*vs[X31] - vs[X03]*vs[X12]*vs[X31] + vs[X03]*vs[X11]*vs[X32] - vs[X01]*vs[X13]*vs[X32] - vs[X02]*vs[X11]*vs[X33] + vs[X01]*vs[X12]*vs[X33]) / d,
            (vs[X03]*vs[X12]*vs[X21] - vs[X02]*vs[X13]*vs[X21] - vs[X03]*vs[X11]*vs[X22] + vs[X01]*vs[X13]*vs[X22] + vs[X02]*vs[X11]*vs[X23] - vs[X01]*vs[X12]*vs[X23]) / d,
            (vs[X13]*vs[X22]*vs[X30] - vs[X12]*vs[X23]*vs[X30] - vs[X13]*vs[X20]*vs[X32] + vs[X10]*vs[X23]*vs[X32] + vs[X12]*vs[X20]*vs[X33] - vs[X10]*vs[X22]*vs[X33]) / d,
            (vs[X02]*vs[X23]*vs[X30] - vs[X03]*vs[X22]*vs[X30] + vs[X03]*vs[X20]*vs[X32] - vs[X00]*vs[X23]*vs[X32] - vs[X02]*vs[X20]*vs[X33] + vs[X00]*vs[X22]*vs[X33]) / d,
            (vs[X03]*vs[X12]*vs[X30] - vs[X02]*vs[X13]*vs[X30] - vs[X03]*vs[X10]*vs[X32] + vs[X00]*vs[X13]*vs[X32] + vs[X02]*vs[X10]*vs[X33] - vs[X00]*vs[X12]*vs[X33]) / d,
            (vs[X02]*vs[X13]*vs[X20] - vs[X03]*vs[X12]*vs[X20] + vs[X03]*vs[X10]*vs[X22] - vs[X00]*vs[X13]*vs[X22] - vs[X02]*vs[X10]*vs[X23] + vs[X00]*vs[X12]*vs[X23]) / d,
            (vs[X11]*vs[X23]*vs[X30] - vs[X13]*vs[X21]*vs[X30] + vs[X13]*vs[X20]*vs[X31] - vs[X10]*vs[X23]*vs[X31] - vs[X11]*vs[X20]*vs[X33] + vs[X10]*vs[X21]*vs[X33]) / d,
            (vs[X03]*vs[X21]*vs[X30] - vs[X01]*vs[X23]*vs[X30] - vs[X03]*vs[X20]*vs[X31] + vs[X00]*vs[X23]*vs[X31] + vs[X01]*vs[X20]*vs[X33] - vs[X00]*vs[X21]*vs[X33]) / d,
            (vs[X01]*vs[X13]*vs[X30] - vs[X03]*vs[X11]*vs[X30] + vs[X03]*vs[X10]*vs[X31] - vs[X00]*vs[X13]*vs[X31] - vs[X01]*vs[X10]*vs[X33] + vs[X00]*vs[X11]*vs[X33]) / d,
            (vs[X03]*vs[X11]*vs[X20] - vs[X01]*vs[X13]*vs[X20] - vs[X03]*vs[X10]*vs[X21] + vs[X00]*vs[X13]*vs[X21] + vs[X01]*vs[X10]*vs[X23] - vs[X00]*vs[X11]*vs[X23]) / d,
            (vs[X12]*vs[X21]*vs[X30] - vs[X11]*vs[X22]*vs[X30] - vs[X12]*vs[X20]*vs[X31] + vs[X10]*vs[X22]*vs[X31] + vs[X11]*vs[X20]*vs[X32] - vs[X10]*vs[X21]*vs[X32]) / d,
            (vs[X01]*vs[X22]*vs[X30] - vs[X02]*vs[X21]*vs[X30] + vs[X02]*vs[X20]*vs[X31] - vs[X00]*vs[X22]*vs[X31] - vs[X01]*vs[X20]*vs[X32] + vs[X00]*vs[X21]*vs[X32]) / d,
            (vs[X02]*vs[X11]*vs[X30] - vs[X01]*vs[X12]*vs[X30] - vs[X02]*vs[X10]*vs[X31] + vs[X00]*vs[X12]*vs[X31] + vs[X01]*vs[X10]*vs[X32] - vs[X00]*vs[X11]*vs[X32]) / d,
            (vs[X01]*vs[X12]*vs[X20] - vs[X02]*vs[X11]*vs[X20] + vs[X02]*vs[X10]*vs[X21] - vs[X00]*vs[X12]*vs[X21] - vs[X01]*vs[X10]*vs[X22] + vs[X00]*vs[X11]*vs[X22]) / d
        );
    }

    public Vec3 mulPosition(Vec3 p) {
        var ps = p.vs;
        return Vec3.of(
                vs[X00]*ps[0] + vs[X01]*ps[1] + vs[X02]*ps[2] + vs[X03],
                vs[X10]*ps[0] + vs[X11]*ps[1] + vs[X12]*ps[2] + vs[X13],
                vs[X20]*ps[0] + vs[X21]*ps[1] + vs[X22]*ps[2] + vs[X23]
        );
    }

    private static final int X00 = 0;
    private static final int X01 = 1;
    private static final int X02 = 2;
    private static final int X03 = 3;


    private static final int X10 = 4;
    private static final int X11 = 5;
    private static final int X12 = 6;
    private static final int X13 = 7;


    private static final int X20 = 8;
    private static final int X21 = 9;
    private static final int X22 = 10;
    private static final int X23 = 11;


    private static final int X30 = 12;
    private static final int X31 = 13;
    private static final int X32 = 14;
    private static final int X33 = 15;

}
