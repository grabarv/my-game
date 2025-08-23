package game.world;

import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static game.world.MapManger.blocksScale;
import static game.world.MapManger.worldBlockZIndex;

public class Wall extends MapItem{
    public Wall(Mesh[] mesh, boolean isEulerRotation, Vector2i posInMap) {
        super(mesh, isEulerRotation, posInMap);
        setScale(MapManger.wallScale);
    }

    @Override
    public void setPosition(Vector2f startPos) {
        setPosition(new Vector3f(startPos.x + getBlockSize().x * mapPosition.x, startPos.y -  getBlockSize().y * mapPosition.y, worldBlockZIndex - 2 * blocksScale));

    }

}
