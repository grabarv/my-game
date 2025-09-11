package game.world;

import engine.Scene;
import engine.graph.Camera;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import utils.Utils;
import utils.loaders.GameFilesLoader;
import utils.loaders.assimp.StaticMeshesLoader;
import utils.loaders.obj.OBJLoader;
import game.records.StructureDescription;
import org.joml.Vector2f;
import org.joml.Vector2i;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Manages the game map, including blocks, walls, and structures.
 * Handles generation, placement, and rendering of map elements
 * into the game {@link Scene}.
 */
public class MapManger {

    /** Map width in blocks. */
    private final int width;

    /** Map height in blocks. */
    private final int height;

    /** Scale factor for block models. */
    public static final float blocksScale = 0.03333333f;

    /** Scale factor for wall models. */
    public static final float wallScale = 0.06666666f;

    /** Z-index (height offset) for blocks in world space. */
    public static float worldBlockZIndex = 1.5f;

    /** Z-index (height offset) for walls in world space. */
    public static float worldWallZIndex = worldBlockZIndex - 2 * blocksScale;

    /** 2D array of map blocks. */
    private final Block[][] blocks;

    /** 2D array of map walls. */
    private final Wall[][] walls;

    /** List of placed structures on the map. */
    private final ArrayList<Structure> structures;

    /** Descriptions of available structures (loaded from file). */
    private final Map<String, StructureDescription> structureDescriptionMap;

    // File paths to models and metadata
    private final String blockObjPath = "/models/cube.obj";
    private final String smallBlockObjPath = "/models/small_cube.obj";
    private final String quadObjPath = "/models/quad.obj";
    private final String tringleCylinderObjectPath = "/models/triangle_cylinder.obj";
    private final String tringleObjectPath = "/models/triangle.obj";
    private final String structureSizeFilePath = "/textures/struct_desc/struct_size.txt";
    private final String pathToModels = "/models/";

    /** Cached meshes for quick object instancing. */
    private Map<String, Mesh[]> meshMap;

    /** Start position of the map (top-left corner in world coordinates). */
    private final Vector2f startPos = new Vector2f(-1.0f, 1.0f);

    /** Objects to generate on the map (by name). */
    private final String[] objects = new String[] {"house1"};

    /**
     * Creates a new {@code MapManger} with given dimensions.
     *
     * @param width  number of blocks in map width
     * @param height number of blocks in map height
     */
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

    /**
     * Loads meshes for all available block, wall, and structure textures.
     * Uses the OBJLoader and assigns materials (including normal maps if available).
     */
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

    /**
     * Generates the map: base blocks and pre-defined obj`ects,
     * then adds them to the scene.
     *
     * @param scene game scene to populate with map items
     */
    public void generateMap(Scene scene) {
        generateBaseMap();
        generateObjects();
        addNewObjectsToScene(scene);
    }

    /**
     * Generates the base layer of the map (e.g. grass/dirt blocks).
     */
    private void generateBaseMap() {
        int halfOfHeight = (int) Math.floor((double) height /2);
        for(int i = 0; i < width; i++) {
            for(int j = halfOfHeight; j < height ; j++){

                if(j == halfOfHeight) {
                    if(i % 2 == 0) {
                        Structure structure = new Structure(meshMap.get("struct_grass"), true, "grass",
                                new Vector2i(i, j-1),
                                structureDescriptionMap.get("grass").size(),
                                worldBlockZIndex,
                                structureDescriptionMap.get("grass").canMoveThough(),
                                structureDescriptionMap.get("grass").canStandOn(),
                                structureDescriptionMap.get("grass").modelSize());
                        structure.setRotation(new Quaternionf(90, 0,0,0));
                        structures.add(structure);
                        blocks[i][j].setMeshes(meshMap.get("dirt"));
                    }

                } else {
                    blocks[i][j].setMeshes(meshMap.get("dirt"));
                }
            }
        }
    }

    /**
     * Generates additional objects (houses, etc.) from predefined names.
     */
    private void generateObjects() {
        for (String object : objects) {
            Vector2i startPos = new Vector2i(width/2 - 20, height/2 - 30);
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

    /**
     * Places the player at the approximate center of the map.
     * (Note: positioning is not perfectly centered, see TODO in code).
     *
     * @param camera game camera to update position
     * @param player main player to place
     */
    public void putPlayerOnMapCenter(Camera camera, MainPlayer player) {
        float xPos = getMapTopLeftCorner().x + width*blocks[0][0].getSize().x/2;
        float yPos = getMapTopLeftCorner().y - 20*blocks[0][0].getSize().y/2 ;
        camera.setPosition(xPos, yPos, camera.getPosition().z);
        player.setPosition(xPos, yPos, player.getPosition().z);
    }

    /**
     * Adds all generated blocks, walls, and structures into the scene.
     *
     * @param scene game scene
     */
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

    /** @return map width in blocks */
    public int getWidth() {
        return width;
    }

    /** @return map height in blocks */
    public int getHeight() {
        return height;
    }

    /** @return 2D block array of the map */
    public Block[][] getBlocks() {
        return blocks;
    }

    /** @return starting position (top-left corner) of the map */
    public Vector2f getStartPos() {
        return startPos;
    }

    /** @return block scale factor */
    public float getBlocksScale() {
        return blocksScale;
    }

    /**
     * Checks if a block exists at given map coordinates.
     *
     * @param x width index
     * @param y height index
     * @return true if block exists and has meshes, false otherwise
     */
    public boolean isThereABlock(int x, int y) {
        if(!checkBlockCoords(x,y)) {
            return false;
        }
        return blocks[x][y] != null && blocks[x][y].getMeshes() != null;
    }

    /**
     * @return world position of top-left map corner
     */
    public Vector2f getMapTopLeftCorner() {
        return new Vector2f(getStartPos().x - blocks[0][0].getSize().x/2f, getStartPos().y + blocks[0][0].getSize().y/2f);
    }

    /**
     * Checks if given block coordinates are inside the map bounds.
     *
     * @param x block x index
     * @param y block y index
     * @return true if coordinates are valid, false otherwise
     */
    public boolean checkBlockCoords(int x, int y) {
        return  !(x < 0 || y < 0 || x >= getWidth() || y >= getHeight());
    }

    /**
     * Checks if given vector coordinates are inside the map bounds.
     *
     * @param coords vector of (x,y) block indices
     * @return true if inside, false otherwise
     */
    public boolean checkBlockCoords(Vector2i coords) {
        return checkBlockCoords(coords.x, coords.y);
    }

    /**
     * Clears an area of the map by removing blocks, walls, and structures.
     *
     * @param startOfArea top-left corner of area
     * @param areaSize    size of area in blocks
     */
    private void clearMapArea(Vector2i startOfArea, Vector2i areaSize) {
        for(int x = 0; x < areaSize.x; x++) {
            for(int y = 0; y < areaSize.y; y++) {
                Block block = new Block(null, false, new Vector2i(startOfArea.x + x, startOfArea.y + y));
                blocks[block.mapPosition.x][block.mapPosition.y] = block;
                Wall wall = new Wall(null, false, new Vector2i(startOfArea.x + x, startOfArea.y + y));
                walls[wall.mapPosition.x][wall.mapPosition.y] = wall;
            }
        }
        ArrayList<Structure> structuresToRemove = new ArrayList<>();
        for (Structure structure : structures) {
            if(Utils.intersects(structure.mapPosition.x, structure.mapPosition.y, structure.getSizeInBlocks().x, structure.getSizeInBlocks().y,
                    startOfArea.x, startOfArea.y, areaSize.x, areaSize.y)) {
                structuresToRemove.add(structure);
            }
        }
        for (Structure removeStructure : structuresToRemove) {
            structures.remove(removeStructure);
        }
    }

    /**
     * @return size of one block in world units (x,y)
     */
    public static Vector2f getBlockSize() {
        return new Vector2f(2f * blocksScale, 2f * blocksScale);
    }


}
