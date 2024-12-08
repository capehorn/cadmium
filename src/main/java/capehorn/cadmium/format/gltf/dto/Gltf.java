package capehorn.cadmium.format.gltf.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

/**
 * The root object for a glTF asset.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gltf {
    private int scene;

    /**
     * A list of buffers. A buffer points to binary geometry, animation, or skins.
     */
    private List<Buffer> buffers;

    /**
     * A list of bufferViews. A bufferView is a view into a buffer generally representing a subset of the buffer.
     */
    private List<Buffer> bufferViews;
    private List<Mesh> meshes;
    private List<Node> nodes;
    private List<Scene> scenes;

    /**
     * Metadata about the glTF asset.
     */
    @NotNull
    private Asset asset;

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    public List<Buffer> getBuffers() {
        return buffers;
    }

    public void setBuffers(List<Buffer> buffers) {
        this.buffers = buffers;
    }

    public List<Buffer> getBufferViews() {
        return bufferViews;
    }

    public void setBufferViews(List<Buffer> bufferViews) {
        this.bufferViews = bufferViews;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public void setMeshes(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }
}
