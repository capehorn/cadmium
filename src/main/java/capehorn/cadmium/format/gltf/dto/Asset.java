package capehorn.cadmium.format.gltf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Metadata about the glTF asset.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Asset {
    /**
     * A copyright message suitable for display to credit the content creator.
     */
    private String copyright;

    /**
     * Tool that generated this glTF model.  Useful for debugging.
     */
    private String generator;

    /**
     * The glTF version in the form of `<major>.<minor>` that this asset targets.
     */
    @NotNull
    @Pattern(regexp = "^[0-9]+\\.[0-9]+$")
    private String version;

    /**
     * The minimum glTF version in the form of `<major>.<minor>` that this asset targets.
     * This property **MUST NOT** be greater than the asset version.
     */
    @Pattern(regexp = "^[0-9]+\\.[0-9]+$")
    private String minVersion;

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }
}
