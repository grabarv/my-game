package game.world.map;

import engine.graph.Mesh;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static game.world.map.MapManager.*;

public class Wall extends MapItem {
    public Wall(Mesh[] mesh, boolean isEulerRotation, Vector2i posInMap) {
        super(mesh, isEulerRotation, posInMap);
        setScale(MapManager.wallScale);
    }

    @Override
    public void setPosition() {
        setPosition(new Vector3f(MapManager.startPos.x + Block.get2DSize().x * mapPosition.x, MapManager.startPos.y -  Block.get2DSize().y * mapPosition.y, worldBlockZIndex - 2 * blocksScale));

    }

}
