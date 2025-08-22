package game.world;

import engine.graph.Mesh;
import org.joml.Vector2i;

public class Wall extends MapItem{
    public Wall(Mesh[] mesh, boolean isEulerRotation, Vector2i posInMap) {
        super(mesh, isEulerRotation);
        setScale(MapManger.blocksScale);
        modelWidth = 2f;
        modelHeight = 2f;
        mapPosition = posInMap;
    }
}
