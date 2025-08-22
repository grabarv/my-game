package game.world;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.items.GameItem;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Block extends GameItem {


    private boolean isInScene = false;

    private Vector2i mapPosition;




    public Block(Mesh[] mesh, boolean isEulerRotation, Vector2i posInMap) {
        super(mesh, isEulerRotation);
        setScale(MapManger.blocksScale);
        modelWidth = 2f;
        modelHeight = 2f;
        mapPosition = posInMap;
    }

    public boolean getIsInScene() {
        return isInScene;
    }
    public void setIsInScene(boolean isInScene) {
        this.isInScene = isInScene;
    }

    public Vector2f getBlockSize() {
        return new Vector2f(modelWidth * getScale(), modelHeight * getScale());
    }

    public Vector2i getMapPosition() {
        return mapPosition;
    }
}
