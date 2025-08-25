package utils.loaders;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import utils.MathEvaluator;
import utils.Utils;
import engine.graph.Mesh;
import game.records.StructureDescription;
import game.world.*;
import org.joml.Vector2i;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameFilesLoader {

    static Map<String, Float> specialValues;

    public static GameObject loadGameObject(String name, Map<String, Mesh[]> meshMap, Map<String, StructureDescription> structureDescriptionMap,  Vector2i startPos)  {
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
                } else if(arguments[0].equalsIgnoreCase("b")){
                    String type = arguments[1];
                    int x = Integer.parseInt(arguments[2]);
                    int y = Integer.parseInt(arguments[3]);
                    Quaternionf rotation = checkRotation(arguments);
                    if(type.startsWith("wall_")) {
                        Wall wall =
                            new Wall(meshMap.getOrDefault(type, null), true,
                                    new Vector2i(startPos.x + x, startPos.y + y));
                        object.addWall(wall);
                        wall.setRotation(rotation);
                    } else {

                        Block block = new Block(meshMap.getOrDefault(type, null), true,
                                new Vector2i(startPos.x + x, startPos.y + y));
                        object.addBlock(block);
                        block.setRotation(rotation);
                    }
                } else if(arguments[0].equalsIgnoreCase("ba")) {
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
                                Block block = new Block(meshMap.getOrDefault(type, null), true,
                                        new Vector2i(startPos.x + x, startPos.y + y));

                                block.setRotation(rotation);
                                object.addBlock(block);
                            }
                        }
                    }
                } else if(arguments[0].equalsIgnoreCase("s")){
                    String type = arguments[1];
                    float zIndex = (float) MathEvaluator.eval(arguments[2], specialValues);
                    boolean canMoveThrow = Boolean.parseBoolean(arguments[3]);
                    int x = Integer.parseInt(arguments[4]);
                    int y = Integer.parseInt(arguments[5]);
                    Quaternionf rotation = checkRotation(arguments);
                    Vector2i size;
                    Vector3f modelSize;
                    StructureDescription structureDescription = structureDescriptionMap.getOrDefault(type.substring(7), null);
                    if(structureDescription != null) {
                        size = structureDescription.size();
                        modelSize = structureDescription.modelSize();
                    } else {
                        size = new Vector2i(1,1);
                        modelSize = new Vector3f(1,1,1);
                    }
                    Structure structure = new Structure(meshMap.getOrDefault(type, null), true,
                            new Vector2i(startPos.x + x, startPos.y + y), size,zIndex, canMoveThrow, modelSize);
                    structure.setRotation(rotation);
                    object.addStructure(structure);
                }
            }
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, StructureDescription> loadStructDescription(String path) {
        Map<String, StructureDescription> result = new HashMap<>();

        try {
            String[] file = Utils.loadResource(path).split("\n");
            for (String line: file) {
                if(line.startsWith("#") || line.isBlank()) {
                    continue;
                }
                String[] arguments = line.split(" ");
                checkLength(arguments.length, 6);
                String[] size = arguments[5].split(",");
                checkLength(size.length, 3);
                StructureDescription structureDescription = new StructureDescription(new Vector2i(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2])),
                        arguments[3], Boolean.parseBoolean(arguments[4]),
                        new Vector3f(Float.parseFloat(size[0]), Float.parseFloat(size[1]), Float.parseFloat(size[1])));
                result.put(arguments[0], structureDescription);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static void checkLength(int amount, int min) {
        if(amount < min) {
            throw new RuntimeException("Wrong game object structure");
        }
    }

    /**
     *
     * @param arguments all arguments from one line
     * @return Returns rotation or -1.0f if rotation is not possible for this block
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
     * Returns -1 if wrong type specified
     *
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

