package capehorn.cadmium.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Vec3Test {

    @Test
    void of() {
        var v = Vec3.of(1, 2, 3);
        assertThat(v.x()).isEqualTo(1);
        assertThat(v.y()).isEqualTo(2);
        assertThat(v.z()).isEqualTo(3);
    }

    @Test
    void testEquals() {
        var a = Vec3.of(1, 2, 3);
        var b = Vec3.of(1, 2, 3);
        assertThat(a.equals(b)).isTrue();
    }

    @Test
    void ofSeries() {
        assertThat(Vec3.ofSeries(1, 2, 3, 4, 5, 6))
                .containsExactly(Vec3.of(1, 2, 3), Vec3.of(4, 5, 6));
    }

    @Test
    void copy() {
        assertThat(Vec3.of(1, 2, 3).copy()).isEqualTo(Vec3.of(1, 2, 3));
    }

    @Test
    void toVec4() {
        assertThat(Vec3.of(1, 2, 3).toVec4(4)).isEqualTo(Vec4.of(1, 2, 3, 4));
    }

    @Test
    void abs() {
        assertThat(Vec3.of(-1, -2, -3).abs()).isEqualTo(Vec3.of(1, 2, 3));
    }

    @Test
    void add() {
        assertThat(Vec3.of(1, 2, 3).add(Vec3.of(-1, -2, 3))).isEqualTo(Vec3.of(0, 0, 6));
    }

    @Test
    void sub() {
        assertThat(Vec3.of(1, 2, 3).sub(Vec3.of(0, 1, 5))).isEqualTo(Vec3.of(1, 1, -2));
    }

    @Test
    void mul() {
        assertThat(Vec3.of(1, 2, 3).mul(Vec3.of(1, 2, 3))).isEqualTo(Vec3.of(1, 4, 9));
    }

    @Test
    void div() {
        assertThat(Vec3.of(1, 2, 16).div(Vec3.of(2, 2, 4))).isEqualTo(Vec3.of(0.5, 1, 4));
    }

    @Test
    void length() {
        assertThat(Vec3.of(2, 3, 4).length()).isEqualTo(5.385164807134504);
    }

    @Test
    void negate() {
        assertThat(Vec3.of(-1, 2, -3).negate()).isEqualTo(Vec3.of(1, -2, 3));
    }

    @Test
    void normalize() {
    }

    @Test
    void dot() {
    }

    @Test
    void cross() {
    }

    @Test
    void lerp() {
    }

    @Test
    void lerpDistance() {
    }

    @Test
    void mod() {
    }

    @Test
    void addScalar() {
    }

    @Test
    void subScalar() {
    }

    @Test
    void mulScalar() {
    }

    @Test
    void divScalar() {
    }

    @Test
    void min() {
    }

    @Test
    void max() {
    }

    @Test
    void floor() {
    }

    @Test
    void ceil() {
    }

    @Test
    void round() {
    }

    @Test
    void roundPlaces() {
    }

    @Test
    void minComponent() {
    }

    @Test
    void maxComponent() {
    }

    @Test
    void reflect() {
    }

    @Test
    void perpendicular() {
    }

    @Test
    void distance() {
    }

    @Test
    void lengthSquared() {
    }

    @Test
    void distanceSquared() {
    }

    @Test
    void segmentDistance() {
    }
}