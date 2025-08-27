package game.world;

import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static game.world.MapManger.getBlockSize;

public class Structure extends MapItem {

    private float zIndex;

    /**
     * Can player or any other character move throw it
     */
    private boolean canMoveThrow;

    private Vector2i sizeInBlocks;

    private String name;

//    public Structure(Mesh[] meshes, boolean isEulerRotation, Vector2i positionInMap, Vector2i sizeInBlocks, float zIndex, boolean canMoveThrow) {
//        super(meshes, isEulerRotation, positionInMap);
//        this.sizeInBlocks = sizeInBlocks;
//        setScale(MapManger.blocksScale * sizeInBlocks.y);
//
//        this.zIndex = zIndex;
//        this.canMoveThrow = canMoveThrow;
//    }
     public Structure(Mesh[] meshes, boolean isEulerRotation,String name,  Vector2i positionInMap, Vector2i sizeInBlocks, float zIndex, boolean canMoveThrow, Vector3f modelSize) {
         super(meshes, isEulerRotation, positionInMap);
         this.name = name;
         this.sizeInBlocks = sizeInBlocks;
         this.zIndex = zIndex;
         this.canMoveThrow = canMoveThrow;
         modelWidth = modelSize.x;
         modelHeight = modelSize.y;
         modelLength = modelSize.z;
         setScale(getBlockSize().y * sizeInBlocks.y / modelHeight);
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPosition(Vector2f topLeftMapCorner) {
        Vector2f structureTopLeftCorner = new Vector2f(topLeftMapCorner.x + mapPosition.x * getBlockSize().x,
                topLeftMapCorner.y - mapPosition.y * getBlockSize().y);
        setPosition(structureTopLeftCorner.x + sizeInBlocks.x * getBlockSize().x / 2,
                structureTopLeftCorner.y - sizeInBlocks.y * getBlockSize().y /2, zIndex);
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
}
