package game.world;

import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class Block extends MapItem {

    public Block(Mesh[] mesh, boolean isEulerRotation, Vector2i posInMap) {
        super(mesh, isEulerRotation);
        setScale(MapManger.blocksScale);
        modelWidth = 2f;
        modelHeight = 2f;
        mapPosition = posInMap;
    }

}
