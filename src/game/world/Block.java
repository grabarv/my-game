package game.world;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.items.GameItem;
import org.joml.Vector2f;

public class Block extends GameItem {


    private boolean isInScene = false;




    public Block(Mesh[] mesh, boolean isEulerRotation, float scale) {
        super(mesh, isEulerRotation);
        setScale(scale);
        modelWidth = 2f;
        modelHeight = 2f;
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


}
