package game.world;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.items.GameItem;

public class Block extends GameItem {

    private final Material material;

    private boolean isInScene = false;

    public Block(Mesh[] mesh, boolean isEulerRotation, Material material, float scale) {
        super(mesh, isEulerRotation);
        this.material = material;
        for (Mesh m : mesh) {
            m.setMaterial(material);
        }
        setScale(scale);
    }

    public boolean getIsInScene() {
        return isInScene;
    }
    public void setIsInScene(boolean isInScene) {
        this.isInScene = isInScene;
    }

    public Material getMaterial() {
        return material;
    }
}
