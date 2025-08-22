package game.world;

import engine.Scene;
import engine.graph.Camera;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.GameObjectLoader;
import engine.loaders.obj.OBJLoader;
import game.MainPlayer;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MapManger {

    private final int width;
    private final int height;

    public static float worldBlockZIndex = 1.5f;
    public static float worldWallZIndex = 2.5f;
    private final Block[][] blocks;
    private final Wall[][] walls;
    private final String blockObjPath = "/models/cube.obj";
    private final String smallBlockObjPPath = "/models/small_cube.obj";
    private final String tringleObjectPath = "/models/triangle_cylinder.obj";
    private Map<String, Mesh[]> meshMap;
    public static final float blocksScale =  0.03333333f;
    public static final float wallScale = 0.03333333f;
    /**
     * The start position is in the left top corner
     */
    private final Vector3f startPos = new Vector3f(-1.0f, 1.0f, worldBlockZIndex - blocksScale);

    private final String[] objects = new String[] {"house1"};


    public MapManger(int width, int height) {
        this.width = width;
        this.height = height;
        blocks = new Block[width][height];
        walls = new Wall[width][height];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                blocks[i][j] = new Block(null, true, new Vector2i(i, j));
                walls[i][j] = new Wall(null, true, new Vector2i(i, j));
            }
        }
        meshMap = new HashMap<>();
        loadMeshesToMap();
    }

    private void loadMeshesToMap() {
            String dirPath = "resources/textures/blocks/";
            Path dir = Paths.get(dirPath);



        try {
            for (Path path : (Iterable<Path>) Files.list(dir).filter(Files::isRegularFile)::iterator) {
                try {
                    String fileName = String.valueOf(path.getFileName());
                    int dotIndex = fileName.lastIndexOf('.');

                    // Remove extension if present
                    String nameWithoutExt = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);

                    Material m = new Material(new Texture(dirPath + fileName));



                    meshMap.put(nameWithoutExt, new Mesh[] {OBJLoader.loadMesh(blockObjPath, 100)});
                    meshMap.put(nameWithoutExt + "_triangle", new Mesh[] {OBJLoader.loadMesh(tringleObjectPath, 100)});
                    for (Mesh mesh : meshMap.get(nameWithoutExt)) {
                        mesh.setMaterial(m);
                    }
                    for(Mesh mesh: meshMap.get(nameWithoutExt + "_triangle")) {
                        mesh.setMaterial(m);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateMap(Scene scene) {

        addBlocksToMap();

        generateObject();

        addNewBlocksToScene(scene);
//        for(int i = (int) Math.floor((double) width /2); i < (int) Math.floor((double) width /2) + 1; i++) {
//            for(int j = (int) Math.floor((double) height /2); j <  (int) Math.floor((double) height /2) + 1 ; j++){
//                blocks[i][j].setMeshes(meshMap.get("dirt"));
//            }
//        }
    }

    private void addBlocksToMap() {
        for(int i = 0; i < width; i++) {
            for(int j = (int) Math.floor((double) height /2); j <  height ; j++){
                blocks[i][j].setMeshes(meshMap.get("dirt"));
            }
        }
    }

    private void generateObject() {
        for (String object : objects) {
            ArrayList<Block> objectBlocks = GameObjectLoader.load(object,
                    meshMap, new Vector2i(20, 0));
            for (Block block : objectBlocks) {
                blocks[block.getMapPosition().x][block.getMapPosition().y] = block;
            }
        }
    }



    // TODO: method has a bug, it does not set player exactly at the center.

    public void putPlayerOnMapCenter(Camera camera, MainPlayer player) {
        float xPos = getMapTopLeftCorner().x; //+ width*blocks[0][0].getSize().x/2;
        float yPos = startPos.y ;
        camera.setPosition(xPos, yPos, camera.getPosition().z);
        player.setPosition(xPos, yPos, player.getPosition().z);
    }

    public void addNewBlocksToScene(Scene scene) {

        for(int i = 0; i < width; i++ ) {
            for (int j = 0; j < height; j++) {
                if(blocks[i][j] != null && blocks[i][j].getMeshes() != null && !blocks[i][j].getIsInScene()) {
                    blocks[i][j].setPosition(new Vector3f(startPos.x + blocks[i][j].getSize().x *i, startPos.y -  blocks[i][j].getSize().y * j, startPos.z));
                    blocks[i][j].setIsInScene(true);
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
        return new Vector3f(getStartPos().x - blocks[0][0].getSize().x/2f, getStartPos().y + blocks[0][0].getSize().y/2f, getStartPos().z);
    }

    public boolean checkBlockCoords(int x, int y) {
        return  !(x < 0 || y < 0 || x >= getWidth() || y >= getHeight());
    }

    public boolean checkBlockCoords(Vector2i coords) {
        return checkBlockCoords(coords.x, coords.y);
    }
}
