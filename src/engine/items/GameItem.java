package engine.items;

import utils.Utils;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import engine.graph.Mesh;

public class GameItem {

    private boolean selected;

    private Mesh[] meshes;

    private final Vector3f position;

    private float scale;

    /**
     * if usingEulerRotation = true, then x,y,z means rotation along axes and w is always 0
     */
    private final Quaternionf rotation;

    private int textPos;
    
    private boolean disableFrustumCulling;

    private boolean insideFrustum;

    private final boolean usingEulerRotation;

    protected float modelHeight = 1f;
    protected float modelWidth = 1f;
    protected float modelLength = 1f;

    public GameItem(boolean isEulerRotation) {
        this.usingEulerRotation = isEulerRotation;
        selected = false;
        position = new Vector3f();
        scale = 1;
        rotation = new Quaternionf();
        textPos = 0;
        insideFrustum = true;
        disableFrustumCulling = false;
    }

    public GameItem(Mesh mesh, boolean isEulerRotation) {
        this(isEulerRotation);
        this.meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes, boolean isEulerRotation) {
        this(isEulerRotation);
        this.meshes = meshes;
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getTextPos() {
        return textPos;
    }

    public boolean isSelected() {
        return selected;
    }

    public final void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setPosition(Vector3f pos) {
        this.position.x = pos.x;
        this.position.y = pos.y;
        this.position.z = pos.z;
    }

    public float getScale() {
        return scale;
    }

    public final void setScale(float scale) {
        this.scale = scale;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public final void setRotation(Quaternionf q) {
        this.rotation.set(q);
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    public void cleanup() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public boolean isInsideFrustum() {
        return insideFrustum;
    }

    public void setInsideFrustum(boolean insideFrustum) {
        this.insideFrustum = insideFrustum;
    }
    
    public boolean isDisableFrustumCulling() {
        return disableFrustumCulling;
    }

    public void setDisableFrustumCulling(boolean disableFrustumCulling) {
        this.disableFrustumCulling = disableFrustumCulling;
    }

    public  boolean usingEulerRotation() {
        return usingEulerRotation;
    }

    /**
     * @param corner possible values: {@code upleft}, {@code upright}, {@code downleft}, {@code downright}
     * @return the position of the specified element {@code corner} in the world
     */
    public Vector3f getCorner(String corner) {
        corner = corner.toLowerCase();
        if(!Utils.isStringInArray(corner, new String[] {"upleft", "upright", "downleft", "downright"})){
            throw new IllegalArgumentException("Wrong input");
        }
        Vector3f result = new Vector3f(getPosition().x, getPosition().y, getPosition().z);
        if(corner.startsWith("up")) {
            result.y += getScale() /2f * (modelHeight == 0 ? 1 : modelHeight);
        } else {
            result.y -= getScale() /2f * (modelHeight == 0 ? 1 : modelHeight);
        }
        if(corner.endsWith("left")) {
            result.x -= getScale() /2f * (modelWidth == 0 ? 1 : modelWidth);
        } else {
            result.x += getScale() /2f * (modelWidth == 0 ? 1 : modelWidth);
        }
        return result;
    }
    public float getModelHeight() {
        return modelHeight;
    }

    public float getModelWidth() {
        return modelWidth;
    }
}
