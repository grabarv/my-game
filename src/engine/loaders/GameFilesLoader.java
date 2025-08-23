package engine.loaders;

import engine.Utils;
import engine.graph.Mesh;
import game.records.StructureDescription;
import game.world.*;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class GameFilesLoader {

    static Map<String, Float> specialValues;

    public static GameObject loadGameObject(String name, Map<String, Mesh[]> meshMap, Map<String, StructureDescription> structureDescriptionMap,  Vector2i startPos)  {
        specialValues = new HashMap<>();
        specialValues.put("z_block", MapManger.worldBlockZIndex);
        specialValues.put("z_wall", MapManger.worldWallZIndex);
        specialValues.put("block_length", 2f);

        try{
            GameObject object = new GameObject();
            object.setClearArea(false);
            String[] objectFile = Utils.loadResource("/game-objects/" + name + ".gameobj").split("\n");

            int width = 0;
            int height = 0;
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
                        checkLength(width, 1);
                        object.getSize().y = height;
                    } else if(arguments[1].equalsIgnoreCase("clearArea") && arguments[2].equalsIgnoreCase("true")) {
                        object.setClearArea(true);
                    }
                } else if(arguments[0].equalsIgnoreCase("b")){
                    String type = arguments[1];
                    int x = Integer.parseInt(arguments[2]);
                    int y = Integer.parseInt(arguments[3]);
                    float rotation = checkRotation(arguments);
                    if(type.startsWith("wall_")) {
                        Wall wall =
                            new Wall(meshMap.getOrDefault(type, null), true,
                                    new Vector2i(startPos.x + x, startPos.y + y));
                        object.addWall(wall);
                        if(rotation != -1) {
                            wall.getRotation().z = rotation;
                        }
                    } else {
                        Block block = new Block(meshMap.getOrDefault(type, null), true,
                                new Vector2i(startPos.x + x, startPos.y + y));
                        object.addBlock(block);
                        if(rotation != -1) {
                            block.getRotation().z = rotation;
                        }
                    }
                } else if(arguments[0].equalsIgnoreCase("ba")) {
                    String type = arguments[1];
                    int x1 = Integer.parseInt(arguments[2]);
                    int y1 = Integer.parseInt(arguments[3]);
                    int x2 = Integer.parseInt(arguments[4]);
                    int y2 = Integer.parseInt(arguments[5]);
                    float rotation = checkRotation(arguments);
                    if(type.startsWith("wall_")) {
                        for(int x = x1; x <= x2; x++) {
                            for(int y = y1; y <= y2; y++) {
                                Wall wall = new Wall(meshMap.getOrDefault(type, null), true,
                                        new Vector2i(startPos.x + x, startPos.y + y));

                                if(rotation != -1) {
                                    wall.getRotation().z = rotation;
                                }
                                object.addWall(wall);
                            }
                        }
                    } else {
                        for(int x = x1; x <= x2; x++) {
                            for(int y = y1; y <= y2; y++) {
                                Block block = new Block(meshMap.getOrDefault(type, null), true,
                                        new Vector2i(startPos.x + x, startPos.y + y));

                                if(rotation != -1) {
                                    block.getRotation().z = rotation;
                                }
                                object.addBlock(block);
                            }
                        }
                    }
                } else if(arguments[0].equalsIgnoreCase("s") && arguments[1].startsWith("struct_")){
                    String type = arguments[1];
                    float zIndex = Utils.calculate(arguments[2], specialValues);
                    int x = Integer.parseInt(arguments[2]);
                    int y = Integer.parseInt(arguments[3]);
                    float rotation = checkRotation(arguments);
                    StructureDescription structureDescription = structureDescriptionMap.getOrDefault(type, null);
                    if(structureDescription == null) {
                        continue;
                    }
                    Structure structure = new Structure(meshMap.getOrDefault(type, null), true,
                            new Vector2i(startPos.x + x, startPos.y + y), structureDescription.size(),zIndex, structureDescription.canMoveThough());
                    if(rotation != -1) {
                        wall.getRotation().z = rotation;
                    }
                }
            }
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, StructureDescription> loadStructSize(String path) {
        Map<String, StructureDescription> result = new HashMap<>();

        try {
            String[] file = Utils.loadResource(path).split("\n");
            for (String line: file) {
                if(line.startsWith("#") || line.isBlank()) {
                    continue;
                }
                String[] arguments = line.split(" ");
                checkLength(arguments.length, 5);
                StructureDescription structureDescription = new StructureDescription(new Vector2i(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2])),
                        arguments[3], Boolean.parseBoolean(arguments[4]));
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
    private static float checkRotation(String[] arguments) {
        int minLengthWithoutRotation = getMinLengthForInputType(arguments[0]);
        if(arguments.length >= minLengthWithoutRotation + 1) {
            int rotation = Integer.parseInt(arguments[arguments.length-1]);
            if(rotation == 0 || rotation == 90 || rotation == 180 || rotation == 270) {
                return (float) rotation;
            }
        }

        return -1.0f;
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
        return -1;
    }

}

