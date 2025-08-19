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
import java.util.HashMap;
import java.util.Map;

public class MapManger {

    private final int width;
    private final int height;

    public static float worldFirstZIndex = 1.5f;
    private final Block[][] blocks;
    private final String blockObjPath = "resources/models/cube.obj";
    private Map<String, Mesh[]> meshMap;
    private float blockScale =  0.03333333f;
    /**
     * The start position is in the left top corner
     */
    private Vector3f startPos = new Vector3f(-1.0f, 1.0f, worldFirstZIndex - blockScale);

    /**
     * May be incorrect TODO: Check is blocksize mas set properly
     */
    private Vector2f blockSize = new Vector2f(blockScale*2, blockScale*2);
    public MapManger(int width, int height) throws Exception {
        this.width = width;
        this.height = height;
        blocks = new Block[width][height];

        meshMap = new HashMap<>();
        Material m = new Material(new Texture("resources/textures/dirt.png"));
        meshMap.put("dirt", StaticMeshesLoader.load(blockObjPath, ""));
        for (Mesh mesh : meshMap.get("dirt")) {
            mesh.setMaterial(m);
        }
    }

    public void generateMap(Scene scene) {

        for(int i = 0; i < 1 /*width*/; i++) {
            for(int j = 0; j < 1/*(int) Math.floor((double) height /2)*/; j++){

                blocks[i][j] = new Block(meshMap.get("dirt"), false, blockScale);
                blocks[i][j].setPosition(new Vector3f(startPos.x + blockSize.x*i, startPos.y - blockSize.y* j, startPos.z));
            }

        }
        addNewBlocksToScene(scene);
    }

    // TODO: method has a bug, it does not set player exactly at the center.

    public void putPlayerOnMapCenter(Camera camera, MainPlayer player) {
        float xPos = getMapTopLeftCorner().x;// + width*blockSize.x/2;
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

    /**
     *
     * @param x width index in map array
     * @param y height index in map array
     * @return true if there is a block or false if the value in that place in array is null. If specified indexes are negative or bigger then array size then returns false
     */
    public boolean isThereABlock(int x, int y) {
        if(x < 0 || y < 0 || x >= blocks.length || y >= blocks[0].length) {
            return false;
        }
//        System.out.println(x +" " + y);
        return blocks[x][y] != null;
    }


    public Vector3f getMapTopLeftCorner() {
        return new Vector3f(getStartPos().x - blockSize.x/2f, getStartPos().y + blockSize.y/2f, getStartPos().z);
    }
}
