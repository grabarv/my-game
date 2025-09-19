package utils.loaders;

import game.world.map.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import utils.MathEvaluator;
import utils.Utils;
import engine.graph.Mesh;
import game.records.StructureDescription;
import game.world.*;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for loading game object definitions and structure descriptions
 * from external resource files. Provides parsing of custom `.gameobj` files
 * into {@link GameObject}, and reading structure metadata into
 * {@link StructureDescription}.
 */
public class GameFilesLoader {

    /** Special values (constants) used in parsing and evaluation of expressions. */
    static Map<String, Float> specialValues;

    /**
     * Loads a game object from a {@code .gameobj} file, parses its blocks, walls,
     * and structures, and instantiates the corresponding game entities.
     *
     * @param name name of the game object (file name without extension, loaded from {@code /game-objects/})
     * @param meshMap cache of meshes for object instancing
     * @param structureDescriptionMap metadata describing available structures
     * @param startPos top-left position in the map grid where the object is placed
     * @return fully constructed {@link GameObject} instance
     *
     * @throws RuntimeException if file parsing fails or arguments are invalid
     */
    public static GameObject loadGameObject(String name,
                                            Map<String, Mesh[]> meshMap,
                                            Map<String, StructureDescription> structureDescriptionMap,
                                            Vector2i startPos)  {
        specialValues = new HashMap<>();
        specialValues.put("z_block", MapManger.worldBlockZIndex);
        specialValues.put("z_wall", MapManger.worldWallZIndex);
        specialValues.put("block_length", 2f * MapManger.blocksScale);

        try{
            GameObject object = new GameObject();
            object.setClearArea(false);
            String[] objectFile = Utils.loadResource("/game-objects/" + name + ".gameobj").split("\n");

            int width;
            int height;
            for (String line : objectFile) {
                if(line.startsWith("#") || line.isBlank()) {
                    continue;
                }
                String[] arguments = line.split(" ");
                checkLength(arguments.length, getMinLengthForInputType(arguments[0]));

                // Metadata block
                if(arguments[0].equalsIgnoreCase("m")) {
                    if(arguments[1].equalsIgnoreCase("name")) {
                        object.setName(arguments[2]);
                    } else if(arguments[1].equalsIgnoreCase("width")) {
                        width = Integer.parseInt(arguments[2]);
                        checkLength(width, 1);
                        object.getSize().x = width;
                    } else if(arguments[1].equalsIgnoreCase("height")) {
                        height = Integer.parseInt(arguments[2]);
                        checkLength(height, 1);
                        object.getSize().y = height;
                    } else if(arguments[1].equalsIgnoreCase("clearArea") && arguments[2].equalsIgnoreCase("true")) {
                        object.setClearArea(true);
                    }
                }
                // Single block or wall
                else if(arguments[0].equalsIgnoreCase("b")){
                    String type = arguments[1];
                    int x = Integer.parseInt(arguments[2]);
                    int y = Integer.parseInt(arguments[3]);
                    Quaternionf rotation = checkRotation(arguments);
                    if(type.startsWith("wall_")) {
                        Wall wall = new Wall(meshMap.getOrDefault(type, null), true,
                                new Vector2i(startPos.x + x, startPos.y + y));
                        object.addWall(wall);
                        wall.setRotation(rotation);
                    } else {
                        MapItemType mapItemType = MapItemType.QUAD;
                        if(type.startsWith("triangle_")){
                            mapItemType = MapItemType.TRIANGLE;
                        }
                        Block block = new Block(meshMap.getOrDefault(type, null), true,
                                new Vector2i(startPos.x + x, startPos.y + y), mapItemType);
                        object.addBlock(block);
                        block.setRotation(rotation);
                    }
                }
                // Block/wall area (multiple placed in grid)
                else if(arguments[0].equalsIgnoreCase("ba")) {
                    String type = arguments[1];
                    int x1 = Integer.parseInt(arguments[2]);
                    int y1 = Integer.parseInt(arguments[3]);
                    int x2 = Integer.parseInt(arguments[4]);
                    int y2 = Integer.parseInt(arguments[5]);
                    Quaternionf rotation = checkRotation(arguments);

                    if(type.startsWith("wall_")) {
                        for(int x = x1; x <= x2; x++) {
                            for(int y = y1; y <= y2; y++) {
                                Wall wall = new Wall(meshMap.getOrDefault(type, null), true,
                                        new Vector2i(startPos.x + x, startPos.y + y));
                                wall.setRotation(rotation);
                                object.addWall(wall);
                            }
                        }
                    } else {
                        for(int x = x1; x <= x2; x++) {
                            for(int y = y1; y <= y2; y++) {
                                MapItemType mapItemType = MapItemType.QUAD;
                                if(type.startsWith("triangle_")){
                                    mapItemType = MapItemType.TRIANGLE;
                                }
                                Block block = new Block(meshMap.getOrDefault(type, null), true,
                                        new Vector2i(startPos.x + x, startPos.y + y), mapItemType);
                                block.setRotation(rotation);
                                object.addBlock(block);
                            }
                        }
                    }
                }
                // Structure placement
                else if(arguments[0].equalsIgnoreCase("s")){
                    String type = arguments[1];
                    float zIndex = (float) MathEvaluator.eval(arguments[2], specialValues);
                    boolean canMoveThrow = Boolean.parseBoolean(arguments[3]);
                    int x = Integer.parseInt(arguments[4]);
                    int y = Integer.parseInt(arguments[5]);
                    Quaternionf rotation = checkRotation(arguments);

                    Vector2i sizeInBlocks;
                    Vector3f modelSize;
                    boolean canStandOn;
                    String typeWithoutStruct = type;
                    if(typeWithoutStruct.startsWith("struct_")){
                        typeWithoutStruct = type.substring(7);
                    }
                    StructureDescription structureDescription = structureDescriptionMap.getOrDefault(typeWithoutStruct, null);
                    if(structureDescription != null) {
                        sizeInBlocks = structureDescription.size();
                        modelSize = structureDescription.modelSize();
                        canStandOn = structureDescription.canStandOn();
                    } else {
                        sizeInBlocks = new Vector2i(1,1);
                        modelSize = new Vector3f(2,2,2);
                        canStandOn = true;
                    }
                    Structure structure = new Structure(meshMap.getOrDefault(type, null),
                            true, typeWithoutStruct,
                            new Vector2i(startPos.x + x, startPos.y + y),
                            sizeInBlocks, zIndex, canMoveThrow, canStandOn, modelSize);
                    structure.setRotation(rotation);
                    object.addStructure(structure);
                }
            }
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads structure descriptions (size, model path, etc.) from a given text file.
     * Each line in the file defines a structure with metadata.
     *
     * @param path resource path to the description file
     * @return map of structure names to {@link StructureDescription}
     */
    public static Map<String, StructureDescription> loadStructDescription(String path) {
        Map<String, StructureDescription> result = new HashMap<>();

        try {
            String[] file = Utils.loadResource(path).split("\n");
            for (String line: file) {
                if(line.startsWith("#") || line.isBlank()) {
                    continue;
                }
                String[] arguments = line.split(" ");
                checkLength(arguments.length, 7);
                String[] size = arguments[6].split(",");
                checkLength(size.length, 3);

                if(arguments[4].equals("false") && arguments[5].equals("false")) {
                    throw new RuntimeException("Structure cannot be both not move-through and not stand-on");
                }

                StructureDescription structureDescription = new StructureDescription(
                        new Vector2i(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2])),
                        arguments[3],
                        Boolean.parseBoolean(arguments[4]), Boolean.parseBoolean(arguments[5]),
                        new Vector3f(Float.parseFloat(size[0]), Float.parseFloat(size[1]), Float.parseFloat(size[1]))
                );
                result.put(arguments[0], structureDescription);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Ensures that argument count meets the minimum expected length.
     *
     * @param amount actual number of arguments
     * @param min    minimum required number
     * @throws RuntimeException if too few arguments
     */
    private static void checkLength(int amount, int min) {
        if(amount < min) {
            throw new RuntimeException("Wrong game object structure");
        }
    }

    /**
     * Parses and constructs a {@link Quaternionf} rotation from the arguments array,
     * if provided at the end of the line. Rotation values are expected as
     * comma-separated floats (x,y,z[,w]).
     *
     * @param arguments all arguments from one line
     * @return rotation quaternion (default 0,0,0,0 if none provided)
     */
    private static Quaternionf checkRotation(String[] arguments) {
        int minLengthWithoutRotation = getMinLengthForInputType(arguments[0]);
        Quaternionf rotation = new Quaternionf(0f, 0f, 0f, 0f);
        if (arguments.length >= minLengthWithoutRotation + 1) {
            String[] values = arguments[arguments.length-1].split(",");
            checkLength(values.length, 3);
            rotation.x = Float.parseFloat(values[0]);
            rotation.y = Float.parseFloat(values[1]);
            rotation.z = Float.parseFloat(values[2]);
            if(values.length == 4) {
                rotation.w = Float.parseFloat(values[3]);
            }
        }
        return rotation;
    }

    /**
     * Gets the minimum required argument length for a given line type.
     *
     * @param type the line type (e.g. "m", "b", "ba", "s")
     * @return minimum length, or -1 if type is invalid
     */
    private static int getMinLengthForInputType(String type) {
        if(type.equalsIgnoreCase("m")) {
            return 3;
        }
        if(type.equalsIgnoreCase("b")) {
            return 4;
        }
        if(type.equalsIgnoreCase("ba")) {
            return 6;
        }
        if(type.equalsIgnoreCase("s")) {
            return 6;
        }
        return -1;
    }

}
