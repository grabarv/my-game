package game.world;

import engine.Scene;
import engine.Utils;
import engine.graph.Camera;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.assimp.StaticMeshesLoader;
import game.MainPlayer;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

public class MapManger {

    private final int width;
    private final int height;


    private final Block[][] blocks;
    private final String blockObjPath = "resources/models/cube.obj";
    private Mesh[] blockMesh;
    /**
     * The start position is in the left top corner
     */
    private Vector3f startPos = new Vector3f(-1.0f, 1.0f, 1.5f);
    private Vector2f blockSize = new Vector2f(0.033f, 0.033f);
    private float blockScale =  0.033f;
    public MapManger(int width, int height) throws Exception {
        this.width = width;
        this.height = height;
        blocks = new Block[width][height];
        blockMesh = StaticMeshesLoader.load(blockObjPath, "");
    }

    public void generateMap(Scene scene) throws Exception {
        Material m1 = new Material(new Texture("resources/textures/dirt.png"));
//        Material m2 = new Material();
        for(int i = 0; i < width; i++) {
            for(int j = (int) Math.floor(height/2); j < height; j++){

                blocks[i][j] = new Block(blockMesh, false, m1, blockScale);
                blocks[i][j].setPosition(new Vector3f(startPos.x + blockSize.x*i, startPos.y - blockSize.y* j, startPos.z));
            }

        }
        addNewBlocksToScene(scene);
    }


    public void putPlayerOnMapCenter(Camera camera, MainPlayer player) {
        float xPos = startPos.x + width*blockSize.x/2;
        float yPos = startPos.y ;
        camera.setPosition(xPos, yPos, camera.getPosition().z);
        player.setPosition(xPos, yPos, player.getPosition().z);
    }

    public void addNewBlocksToScene(Scene scene) {

        for(int i = 0; i < width; i++ ) {
            for (int j = 0; j < height; j++) {
                if(blocks[i][j] != null && !blocks[i][j].getIsInScene()) {
                    scene.setGameItems(new GameItem[] {blocks[i][j]});
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public Block[][] getBlocks() {
        return blocks;
    }

    public Vector3f getStartPos() {
        return startPos;
    }

    public float getBlockScale() {
        return blockScale;
    }

    public Vector2f getBlockSize() {
        return blockSize;
    }
}
