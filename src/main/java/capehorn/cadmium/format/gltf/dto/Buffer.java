package capehorn.cadmium.format.gltf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Buffer {

    /**
     * The URI (or IRI) of the buffer.
     * Relative paths are relative to the current glTF asset.
     * Instead of referencing an external file, this field **MAY** contain a `data:`-URI.
     */
    private String uri;

    /**
     * The length of the buffer in bytes.
     */
    @Min(1)
    private int byteLength;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getByteLength() {
        return byteLength;
    }

    public void setByteLength(int byteLength) {
        this.byteLength = byteLength;
    }
}
