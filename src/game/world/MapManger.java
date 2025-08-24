package game.world;

import engine.Scene;
import engine.graph.Camera;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import utils.loaders.GameFilesLoader;
import utils.loaders.obj.OBJLoader;
import game.MainPlayer;
import game.records.StructureDescription;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MapManger {

    private final int width;
    private final int height;

    public static final float blocksScale =  0.03333333f;
    public static final float wallScale = 0.06666666f;

    public static float worldBlockZIndex = 1.5f;
    public static float worldWallZIndex = worldBlockZIndex - 2 * blocksScale;

    private final Block[][] blocks;
    private final Wall[][] walls;
    private final ArrayList<Structure> structures;

    private final Map<String, StructureDescription> structureDescriptionMap;

    private final String blockObjPath = "/models/cube.obj";
    private final String smallBlockObjPath = "/models/small_cube.obj";
    private final String quadObjPath = "/models/quad.obj";
    private final String tringleCylinderObjectPath = "/models/triangle_cylinder.obj";
    private final String tringleObjectPath = "/models/triangle.obj";
    private final String structureSizeFilePath = "/textures/struct_desc/struct_size.txt";
    private final String pathToModels = "/models/";
    private Map<String, Mesh[]> meshMap;


  /** The start position is in the left top corner */
  private final Vector2f startPos = new Vector2f(-1.0f, 1.0f);

    private final String[] objects = new String[] {"house1"};


    public MapManger(int width, int height) {
        this.width = width;
        this.height = height;
        structureDescriptionMap = GameFilesLoader.loadStructDescription(structureSizeFilePath);
        blocks = new Block[width][height];
        walls = new Wall[width][height];
        structures = new ArrayList<>();
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
        String dirPath = "resources/textures/map_objects/";
        String dirtPathNormals = "resources/textures/normals/";
        Path dir = Paths.get(dirPath);
        try {
            for (Path path : (Iterable<Path>) Files.list(dir).filter(Files::isRegularFile)::iterator) {
                try {
                    String fileName = String.valueOf(path.getFileName());
                    int dotIndex = fileName.lastIndexOf('.');

                    // Remove extension if present
                    String nameWithoutExt = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);

                    Material m = new Material(new Texture(dirPath + fileName));
                    String normalMapPath = dirtPathNormals + nameWithoutExt + "_normal.png";
                    Path path_normal = Paths.get(normalMapPath);
                    if(Files.exists(path_normal) && Files.isRegularFile(path_normal)) {
                        m.setNormalMap(new Texture(normalMapPath));
                    }
                    if(nameWithoutExt.startsWith("wall_")) {
                        meshMap.put(nameWithoutExt, new Mesh[] {OBJLoader.loadMesh(quadObjPath, 100)});
                        meshMap.put(nameWithoutExt + "_triangle", new Mesh[] {OBJLoader.loadMesh(tringleObjectPath, 100)});
                        for(Mesh mesh: meshMap.get(nameWithoutExt + "_triangle")) {
                            mesh.setMaterial(m);
                        }
                    } else if (nameWithoutExt.startsWith("struct_")) {
                        meshMap.put(nameWithoutExt, new Mesh[] {OBJLoader.loadMesh(
                                pathToModels + structureDescriptionMap.get(nameWithoutExt.substring(7)).objFilePath(), 100)});
                    } else {
                        meshMap.put(nameWithoutExt, new Mesh[] {OBJLoader.loadMesh(blockObjPath, 100)});
                        meshMap.put(nameWithoutExt + "_triangle", new Mesh[] {OBJLoader.loadMesh(tringleCylinderObjectPath, 100)});
                        for(Mesh mesh: meshMap.get(nameWithoutExt + "_triangle")) {
                            mesh.setMaterial(m);
                        }
                    }
                    for (Mesh mesh : meshMap.get(nameWithoutExt)) {
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

        generateBaseMap();

        generateObjects();

        addNewObjectsToScene(scene);
//        for(int i = (int) Math.floor((double) width /2); i < (int) Math.floor((double) width /2) + 1; i++) {
//            for(int j = (int) Math.floor((double) height /2); j <  (int) Math.floor((double) height /2) + 1 ; j++){
//                blocks[i][j].setMeshes(meshMap.get("dirt"));
//            }
//        }
    }

    private void generateBaseMap() {
        for(int i = 0; i < width; i++) {
            for(int j = (int) Math.floor((double) height /2); j <  height ; j++){
                blocks[i][j].setMeshes(meshMap.get("dirt"));
            }
        }
    }

    private void generateObjects() {
        for (String object : objects) {
            Vector2i startPos = new Vector2i(width/2 - 20, height/2 - 31);
            GameObject gameObject = GameFilesLoader.loadGameObject(object,
                    meshMap, structureDescriptionMap, startPos);
            if(gameObject.isClearArea()) {
                clearMapArea(startPos, gameObject.getSize());
            }
            for (Block block : gameObject.getBlocks()) {
                blocks[block.getMapPosition().x][block.getMapPosition().y] = block;
            }
            for (Wall wall : gameObject.getWalls()) {
                walls[wall.getMapPosition().x][wall.getMapPosition().y] = wall;
            }

            structures.addAll(Arrays.asList(gameObject.getStructures()));

        }
    }



    // TODO: method has a bug, it does not set player exactly at the center.

    public void putPlayerOnMapCenter(Camera camera, MainPlayer player) {
        float xPos = getMapTopLeftCorner().x+ width*blocks[0][0].getSize().x/2;
        float yPos = getMapTopLeftCorner().y - 20*blocks[0][0].getSize().y/2 ;
        camera.setPosition(xPos, yPos, camera.getPosition().z);
        player.setPosition(xPos, yPos, player.getPosition().z);
    }

    public void addNewObjectsToScene(Scene scene) {

        for(int i = 0; i < width; i++ ) {
            for (int j = 0; j < height; j++) {
                if(blocks[i][j] != null && blocks[i][j].getMeshes() != null && !blocks[i][j].getIsInScene()) {
                    blocks[i][j].setPosition(startPos);
                    blocks[i][j].setIsInScene(true);
                    scene.setGameItems(new GameItem[] {blocks[i][j]});
                }
                if(walls[i][j] != null && walls[i][j].getMeshes() != null && !walls[i][j].getIsInScene()) {
                    walls[i][j].setPosition(startPos);
                    walls[i][j].setIsInScene(true);
                    scene.setGameItems(new GameItem[] {walls[i][j]});
                }
            }
        }
        for (Structure structure : structures) {
            if(!structure.isInScene) {
                structure.setPosition(getMapTopLeftCorner());
                structure.setIsInScene(true);
                scene.setGameItems(new GameItem[] {structure});
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

    public Vector2f getStartPos() {
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


    public Vector2f getMapTopLeftCorner() {
        return new Vector2f(getStartPos().x - blocks[0][0].getSize().x/2f, getStartPos().y + blocks[0][0].getSize().y/2f);
    }

    public boolean checkBlockCoords(int x, int y) {
        return  !(x < 0 || y < 0 || x >= getWidth() || y >= getHeight());
    }

    public boolean checkBlockCoords(Vector2i coords) {
        return checkBlockCoords(coords.x, coords.y);
    }

    // TODO: Clear also structures
    private void clearMapArea(Vector2i startOfArea, Vector2i areaSize) {
        for(int x = 0; x < areaSize.x; x++) {
            for(int y = 0; y < areaSize.y; y++) {
                Block block = new Block(null, false, new Vector2i(startOfArea.x + x, startOfArea.y + y));
                blocks[block.mapPosition.x][block.mapPosition.y] = block;
                Wall wall = new Wall(null, false, new Vector2i(startOfArea.x + x, startOfArea.y + y));
                walls[wall.mapPosition.x][wall.mapPosition.y] = wall;
            }
        }
    }
}
