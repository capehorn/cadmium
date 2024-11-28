package capehorn.cadmium.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class VecBufferTest {

    @Test
    void createFromVec3s() {
        var b = VecBuffer.of(Vec3.of(0, 0, 0), Vec3.of(1, 0, 0));
        assertOnPositionLimitCapacity(b, 2, 2, 2);
        assertThat(b.get(0)).isEqualTo(new double[]{0, 0 , 0});
        assertThat(b.get(1)).isEqualTo(new double[]{1, 0 , 0});
    }

    @Test
    void createFromVec4s() {
        var b = VecBuffer.of(Vec4.of(1, 2, 3, 4));
        assertOnPositionLimitCapacity(b, 1, 1, 1);
        assertThat(b.get(0)).isEqualTo(new double[]{1, 2, 3, 4});
    }

    @Test
    void put_relative() {
        var b = new VecBuffer(new int[]{3, 3, 3}, 4);
        b.put(
                0, 0, 0,
                1, 0, 0,
                0, 1, 0
        );
        assertOnPositionLimitCapacity(b, 1, 1, 4);
        b.put(
                2, 0, 0,
                0, 2, 0,
                0, 0, 2
        );
        assertOnPositionLimitCapacity(b, 2, 2, 4);
    }

    @Test
    void get_byIndex() {
        var itemA = new double[] {
                0, 0, 0,
                1, 0, 0,
                0, 1, 0
        };
        var itemB = new double[] {
                2, 0, 0,
                0, 2, 0,
                0, 0, 2
        };
        var b = new VecBuffer(new int[]{3, 3, 3}, 4);
        b.put(itemA);
        b.put(itemB);
        assertThat(b.get(0)).isEqualTo(itemA);
        assertThat(b.get(1)).isEqualTo(itemB);
    }

    @Test
    void get_byRelativePosition() {
        var b = new VecBuffer(new int[]{3, 3, 3}, 4);
        var itemA = new double[] {
                0, 0, 0,
                1, 0, 0,
                0, 1, 0
        };
        var itemB = new double[] {
                2, 0, 0,
                0, 2, 0,
                0, 0, 2
        };
        b.put(itemA);
        b.put(itemB);
        b.setPosition(0);
        assertOnPositionLimitCapacity(b, 0, 2, 4);
        assertThat(b.get()).isEqualTo(itemA);
        assertThat(b.get()).isEqualTo(itemB);
    }

    @Test
    void recompute_item() {
        var itemA = new double[] {
                0, 0, 0,
                1, 0, 0,
                0, 1, 0
        };
        var itemB = new double[] {
                2, 0, 0,
                0, 2, 0,
                0, 0, 2
        };
        var b = new VecBuffer(new int[]{3, 3, 3}, 4);
        b.put(itemA);
        b.put(itemB);
        b.recompute(1, item -> DoubleStream.of(item).map(v -> v + 10).toArray());
        assertThat(b.get(1)).isEqualTo(new double[]{
                12, 10, 10,
                10, 12, 10,
                10, 10, 12
        });
        assertOnPositionLimitCapacity(b, 2, 2, 4);
    }

    @Test
    void recomputeAll() {
        var itemA = new double[] {
                0, 0, 0,
                1, 0, 0,
                0, 1, 0
        };
        var itemB = new double[] {
                2, 0, 0,
                0, 2, 0,
                0, 0, 2
        };
        var b = new VecBuffer(new int[]{3, 3, 3}, 4);
        b.put(itemA);
        b.put(itemB);
        b.recompute((i, item) -> DoubleStream.of(item).map(v -> v + 10).toArray());
        assertThat(b.get(0)).isEqualTo(new double[]{
                10, 10, 10,
                11, 10, 10,
                10, 11, 10
        });
        assertThat(b.get(1)).isEqualTo(new double[]{
                12, 10, 10,
                10, 12, 10,
                10, 10, 12
        });
    }

    private void assertOnPositionLimitCapacity(VecBuffer b, int expectedPosition, int expectedLimit, int expectedCapacity) {
        assertThat(b.getPosition()).isEqualTo(expectedPosition);
        assertThat(b.getLimit()).isEqualTo(expectedLimit);
        assertThat(b.getCapacity()).isEqualTo(expectedCapacity);
    }
}