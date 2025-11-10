package game.world.map;

import org.joml.Vector2f;

import java.util.ArrayList;

public interface HasShape {
    ShapeType getShapeType();
    void setShapeType(ShapeType type);

    void setShapeVertex(Vector2f shapeVertices);
    void setShapeVertices(ArrayList<Vector2f> shapeVertices);
    ArrayList<Vector2f> getShapeVertices();
}
