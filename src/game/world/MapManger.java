package game.world;

import engine.Scene;
import engine.graph.Camera;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.assimp.StaticMeshesLoader;
import game.MainPlayer;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class MapManger {

    private final int width;
    private final int height;

    public static float worldFirstZIndex = 1.5f;
    private final Block[][] blocks;
    private final String blockObjPath = "resources/models/cube.obj";
    private Map<String, Mesh[]> meshMap;
    private final float blocksScale =  0.03333333f;
    /**
     * The start position is in the left top corner
     */
    private Vector3f startPos = new Vector3f(-1.0f, 1.0f, worldFirstZIndex - blocksScale);


    public MapManger(int width, int height) throws Exception {
        this.width = width;
        this.height = height;
        blocks = new Block[width][height];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                blocks[i][j] = new Block(null, false, blocksScale);
                blocks[i][j].setPosition(new Vector3f(startPos.x + blocks[i][j].getBlockSize().x *i, startPos.y -  blocks[i][j].getBlockSize().y * j, startPos.z));
                System.out.println(blocks[i][j].getPosition().x + " " + blocks[i][j].getPosition().y);
            }
        }
        meshMap = new HashMap<>();
        Material m = new Material(new Texture("resources/textures/soil.png"));
        meshMap.put("dirt", StaticMeshesLoader.load(blockObjPath, ""));
        for (Mesh mesh : meshMap.get("dirt")) {
            mesh.setMaterial(m);
        }
    }

    public void generateMap(Scene scene) {

        for(int i = 0; i < width; i++) {
            for(int j = (int) Math.floor((double) height /2); j <  height ; j++){
                blocks[i][j].setMeshes(meshMap.get("dirt"));
            }

        }
        addNewBlocksToScene(scene);
    }

    // TODO: method has a bug, it does not set player exactly at the center.

    public void putPlayerOnMapCenter(Camera camera, MainPlayer player) {
        float xPos = getMapTopLeftCorner().x + width*blocks[0][0].getBlockSize().x/2;
        float yPos = startPos.y ;
        camera.setPosition(xPos, yPos, camera.getPosition().z);
        player.setPosition(xPos, yPos, player.getPosition().z);
    }

    public void addNewBlocksToScene(Scene scene) {

        for(int i = 0; i < width; i++ ) {
            for (int j = 0; j < height; j++) {
                if(blocks[i][j] != null && blocks[i][j].getMeshes() != null && !blocks[i][j].getIsInScene()) {
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

    public float getBlocksScale() {
        return blocksScale;
    }



    /**
     *
     * @param x width index in map array
     * @param y height index in map array
     * @return true if there is a block or false if the value in that place in array is null. If specified indexes are negative or bigger then array size then returns false
     */
    public boolean isThereABlock(int x, int y) {
        if(!checkBlockCoords(x,y)) {
            return false;
        }
//        System.out.println(x +" " + y);
        return blocks[x][y] != null && blocks[x][y].getMeshes() != null;
    }


    public Vector3f getMapTopLeftCorner() {
        return new Vector3f(getStartPos().x - blocks[0][0].getBlockSize().x/2f, getStartPos().y + blocks[0][0].getBlockSize().y/2f, getStartPos().z);
    }

    public boolean checkBlockCoords(int x, int y) {
        return  !(x < 0 || y < 0 || x >= getWidth() || y >= getHeight());
    }

    public boolean checkBlockCoords(Vector2i coords) {
        return checkBlockCoords(coords.x, coords.y);
    }
}
