package game.world;

import engine.graph.Mesh;
import engine.items.GameItem;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static game.world.MapManger.blocksScale;

public abstract class MapItem extends GameItem {

    protected boolean isInScene = false;

    protected Vector2i mapPosition;

    public MapItem(Mesh[] meshes, boolean isEulerRotation) {
        super(meshes, isEulerRotation);
        modelWidth = 2f;
        modelHeight = 2f;
        modelLength = 2f;
    }
    public MapItem(Mesh[] meshes, boolean isEulerRotation, Vector3f modelSize) {
        this(meshes, isEulerRotation);
        modelWidth = modelSize.x;
        modelHeight = modelSize.y;
        modelLength = modelSize.z;
    }

    public MapItem(Mesh[] meshes, boolean isEulerRotation, Vector2i positionInMap) {
        this(meshes, isEulerRotation);
        this.mapPosition = positionInMap;
    }

    public MapItem(Mesh[] meshes, boolean isEulerRotation, Vector3f modelSize, Vector2i mapPosition) {
        this(meshes, isEulerRotation, modelSize);
        this.mapPosition = mapPosition;
    }

    public boolean getIsInScene() {
        return isInScene;
    }
    public void setIsInScene(boolean isInScene) {
        this.isInScene = isInScene;
    }

    public Vector3f getSize() {
        return new Vector3f(modelWidth * getScale(), modelHeight * getScale(), modelLength * getScale());
    }

    public Vector2i getMapPosition() {
        return mapPosition;
    }
    public abstract void setPosition(Vector2f startPos);

    protected Vector2f getBlockSize() {
        return new Vector2f(modelWidth * blocksScale, modelHeight * blocksScale);
    }
}
