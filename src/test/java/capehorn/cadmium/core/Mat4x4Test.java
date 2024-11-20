package capehorn.cadmium.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Mat4x4Test {

    private final Mat4x4 matA = new Mat4x4(
            3, -2, 4, 2,
            4, -3, -6, 5,
            9, 6, 7, -4,
            2, 5, 8, -3
    );

    private final Mat4x4 matB = new Mat4x4(
            3, 5, -6, 3,
            6, -3, 4, 8,
            -4, 3, 8, 5,
            -3, 5, 2, 3
    );

    @Test
    void transpose() {
        assertThat(matA.transpose()).isEqualTo(new Mat4x4(
                3, 4, 9, 2,
                -2, -3, 6, 5,
                4, -6, 7, 8,
                2, 5, -4, -3
        ));
        assertThat(matB.transpose()).isEqualTo(new Mat4x4(
                3, 6, -4, -3,
                5, -3, 3, 5,
                -6, 4, 8, 2,
                3, 8, 5, 3
        ));
    }

    @Test
    void mulMatrix() {
        assertThat(Mat4x4.Identity.mul(Mat4x4.Identity)).isEqualTo(Mat4x4.Identity);
        assertThat(matA.mul(matB)).isEqualTo(new Mat4x4(
                -25, 43, 10, 19,
                3, 36, -74,	-27,
                47, 28, 18, 98,
                13, 4, 66, 77
        ));

        assertThat(matB.mul(matA)).isEqualTo(new Mat4x4(
                -19, -42, -36, 46,
                58, 61, 134, -43,
                82, 72, 62, -40,
                35, 18, -4, 2
        ));
    }

    @Test
    void determinant() {
        assertThat(matA.determinant()).isEqualTo(980);
        assertThat(matB.determinant()).isEqualTo(-100);
    }

    @Test
    void inverse() {
        assertThat(matA.inverse()).isEqualTo(new Mat4x4(
                0.044897959183673466, 0.004081632653061225, 0.12244897959183673, -0.12653061224489795,
                -0.22448979591836735, 0.22959183673469388, -0.11224489795918367, 0.3826530612244898,
                0.11836734693877551, -0.03469387755102041, -0.04081632653061224, 0.07551020408163266,
                -0.02857142857142857, 0.29285714285714287, -0.21428571428571427, 0.42142857142857143
        ));
        assertThat(matB.inverse()).isEqualTo(new Mat4x4(
                2.5, -1.28, 3.92, -5.62,
                2.0, -1.08, 3.12, -4.32,
                1.75, -0.96, 2.94, -4.09,
                -2.0, 1.16, -3.24, 4.64
        ));
    }
}
