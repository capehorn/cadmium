package capehorn.cadmium.format.gltf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BufferView {

    public static int TARGET_ARRAY_BUFFER = 34962;
    public static int TARGET_ELEMENT_ARRAY_BUFFER = 34963;

    /**
     * Buffer index
     */
    @Min(0)
    private int buffer;

    /**
     * The offset into the buffer in bytes. (min 0, default 0)
     */
    @Min(0)
    private int byteOffset;

    /**
     * The length of the bufferView in bytes. (min 1)
     */
    @Min(0)
    private int byteLength;

    /**
     * The stride, in bytes. (min 4, max 252, multiple of 4)
     * The stride, in bytes, between vertex attributes.  When this is not defined, data is tightly packed.
     * When two or more accessors use the same buffer view, this field **MUST** be defined.
     */
    @Min(4)
    @Max(252)
    private Integer byteStride;

    /**
     * The hint representing the intended GPU buffer type to use with this buffer view.
     */
    private Integer target;

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public int getByteOffset() {
        return byteOffset;
    }

    public void setByteOffset(int byteOffset) {
        this.byteOffset = byteOffset;
    }

    public int getByteLength() {
        return byteLength;
    }

    public void setByteLength(int byteLength) {
        this.byteLength = byteLength;
    }

    public Integer getByteStride() {
        return byteStride;
    }

    public void setByteStride(Integer byteStride) {
        this.byteStride = byteStride;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }
}
