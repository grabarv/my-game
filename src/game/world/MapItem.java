package game.world;

import engine.graph.Mesh;
import engine.items.GameItem;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class MapItem extends GameItem {

    protected boolean isInScene = false;

    protected Vector2i mapPosition;

    public MapItem(Mesh[] meshes, boolean isEulerRotation) {
        super(meshes, isEulerRotation);
    }

    public boolean getIsInScene() {
        return isInScene;
    }
    public void setIsInScene(boolean isInScene) {
        this.isInScene = isInScene;
    }

    public Vector2f getSize() {
        return new Vector2f(modelWidth * getScale(), modelHeight * getScale());
    }

    public Vector2i getMapPosition() {
        return mapPosition;
    }
}
