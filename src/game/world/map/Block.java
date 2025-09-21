package game.world.map;

import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;

import static game.world.map.MapManger.worldBlockZIndex;

public class Block extends MapItem implements HasShape {

    private ShapeType type;

    private ArrayList<Vector2f> shapeVertices;

    public Block(Mesh[] mesh, boolean isEulerRotation, Vector2i posInMap, ShapeType mapItemType) {
    super(mesh, isEulerRotation, new Vector3f(2f, 2f, 2f), posInMap);
        setScale(MapManger.blocksScale);
        this.type = mapItemType;
    }

    public Block(Mesh[] mesh, boolean isEulerRotation, Vector2i posInMap) {
        this(mesh, isEulerRotation, posInMap, ShapeType.RECTANGLE);
    }




    @Override
    public void setPosition(Vector2f startPos) {
        setPosition(new Vector3f(startPos.x + getSize().x *mapPosition.x, startPos.y -  getSize().y * mapPosition.y, worldBlockZIndex - getScale()));

    }

    @Override
    public void setShapeVertices(ArrayList<Vector2f> shapeVertices) {
        this.shapeVertices = shapeVertices;
    }

    @Override
    public ArrayList<Vector2f> getShapeVertices() {
        return shapeVertices;
    }

    @Override
    public void setShapeVertex(Vector2f shapeVertex) {
        this.shapeVertices.add(shapeVertex);
    }

    @Override
    public void setShapeType(ShapeType type) {
        this.type = type;
    }

    @Override
    public ShapeType getShapeType() {
        return type;
    }
}
