package game.world.map;

import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Structure extends MapItem implements HasShape {

    private float zIndex;

    /**
     * Can player or any other character move throw it
     */
    private boolean canMoveThrow;

    /**
     * Can player or any other character stand on it
     * Must be true if canMoveThrow = false
     */
    private boolean canStandOn;

    private Vector2i sizeInBlocks;

    private String name;


    private ShapeType type;

    private ArrayList<Vector2f> shapeVertices;

//    public Structure(Mesh[] meshes, boolean isEulerRotation, Vector2i positionInMap, Vector2i sizeInBlocks, float zIndex, boolean canMoveThrow) {
//        super(meshes, isEulerRotation, positionInMap);
//        this.sizeInBlocks = sizeInBlocks;
//        setScale(MapManger.blocksScale * sizeInBlocks.y);
//
//        this.zIndex = zIndex;
//        this.canMoveThrow = canMoveThrow;
//    }
     public Structure(Mesh[] meshes, boolean isEulerRotation,String name,  Vector2i positionInMap, Vector2i sizeInBlocks, float zIndex, boolean canMoveThrow,boolean canStandOn, Vector3f modelSize) {
         super(meshes, isEulerRotation, positionInMap);
         this.name = name;
         this.sizeInBlocks = sizeInBlocks;
         this.zIndex = zIndex;
         this.canMoveThrow = canMoveThrow;
         this.canStandOn = canStandOn;
         modelWidth = modelSize.x;
         modelHeight = modelSize.y;
         modelLength = modelSize.z;
         setScale(Block.get2DSize().y * sizeInBlocks.y / modelHeight);
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPosition(Vector2f topLeftMapCorner) {
        Vector2f structureTopLeftCorner = new Vector2f(topLeftMapCorner.x + mapPosition.x * Block.get2DSize().x,
                topLeftMapCorner.y - mapPosition.y * Block.get2DSize().y);
        setPosition(structureTopLeftCorner.x + sizeInBlocks.x * Block.get2DSize().y / 2,
                structureTopLeftCorner.y - sizeInBlocks.y * Block.get2DSize().y /2, zIndex);
    }

    @Override
    public Vector3f getSize() {
        return new Vector3f(modelWidth * getScale() * sizeInBlocks.x, modelHeight * getScale() * sizeInBlocks.y, modelLength * getScale());
    }

    public Vector2i getSizeInBlocks() {
         return sizeInBlocks;
    }

    public void setSizeInBlocks(Vector2i sizeInBlocks) {
        this.sizeInBlocks = sizeInBlocks;
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

    public Vector2f get2DSize() {
        return new Vector2f(getSize().x, getSize().y);
    }

    public boolean canMoveThrow() {
        return canMoveThrow;
    }
}
