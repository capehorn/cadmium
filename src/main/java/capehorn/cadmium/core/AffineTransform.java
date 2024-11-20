package capehorn.cadmium.core;

public interface AffineTransform<T> {
    T transform(Mat4x4 trf);
}
