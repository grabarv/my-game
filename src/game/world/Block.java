package game.world;

import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static game.world.MapManger.worldBlockZIndex;

public class Block extends MapItem {

    public Block(Mesh[] mesh, boolean isEulerRotation, Vector2i posInMap) {
    super(mesh, isEulerRotation, new Vector3f(2f, 2f, 2f), posInMap);
        setScale(MapManger.blocksScale);
    }


    @Override
    public void setPosition(Vector2f startPos) {
        setPosition(new Vector3f(startPos.x + getSize().x *mapPosition.x, startPos.y -  getSize().y * mapPosition.y, worldBlockZIndex - getScale()));

    }

}
