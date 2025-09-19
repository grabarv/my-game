package game.world.map;

import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static game.world.map.MapManger.worldBlockZIndex;

public class Block extends MapItem {

    private MapItemType type;

    public Block(Mesh[] mesh, boolean isEulerRotation, Vector2i posInMap, MapItemType mapItemType) {
    super(mesh, isEulerRotation, new Vector3f(2f, 2f, 2f), posInMap);
        setScale(MapManger.blocksScale);
        this.type = mapItemType;
    }


    @Override
    public void setPosition(Vector2f startPos) {
        setPosition(new Vector3f(startPos.x + getSize().x *mapPosition.x, startPos.y -  getSize().y * mapPosition.y, worldBlockZIndex - getScale()));

    }

    public MapItemType getType() {
        return type;
    }

    public void setType(MapItemType type) {
        this.type = type;
    }
}
