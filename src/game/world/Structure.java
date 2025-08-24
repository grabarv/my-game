package game.world;

import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Structure extends MapItem {

    private float zIndex;

    /**
     * Can player or any other character move throw it
     */
    private boolean canMoveThrow;

    private Vector2i sizeInBlocks;

    public Structure(Mesh[] meshes, boolean isEulerRotation, Vector2i positionInMap, Vector2i sizeInBlocks, float zIndex, boolean canMoveThrow) {
        super(meshes, isEulerRotation, positionInMap);
        setScale(MapManger.blocksScale * sizeInBlocks.y);
        this.sizeInBlocks = sizeInBlocks;
        this.zIndex = zIndex;
        this.canMoveThrow = canMoveThrow;
    }

    @Override
    public void setPosition(Vector2f topLeftMapCorner) {
        Vector2f structureTopLeftCorner = new Vector2f(topLeftMapCorner.x + mapPosition.x * getBlockSize().x,
                topLeftMapCorner.y - mapPosition.y * getBlockSize().y);

        setPosition(structureTopLeftCorner.x + sizeInBlocks.x * getBlockSize().x / 2,
                structureTopLeftCorner.y - sizeInBlocks.y * getBlockSize().y / 2, zIndex);
    }

    @Override
    public Vector3f getSize() {
        return new Vector3f(modelWidth * getScale() * sizeInBlocks.x, modelHeight * getScale() * sizeInBlocks.y, modelLength * getScale());
    }


}
