package game.world;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.items.GameItem;

public class Block extends GameItem {


    private boolean isInScene = false;

    public Block(Mesh[] mesh, boolean isEulerRotation, float scale) {
        super(mesh, isEulerRotation);
        setScale(scale);
    }

    public boolean getIsInScene() {
        return isInScene;
    }
    public void setIsInScene(boolean isInScene) {
        this.isInScene = isInScene;
    }

}
