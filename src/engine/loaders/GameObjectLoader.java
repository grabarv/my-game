package engine.loaders;

import engine.Utils;
import engine.graph.Mesh;
import game.world.Block;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Map;

public class GameObjectLoader {

    public static ArrayList<Block> load(String name, Map<String, Mesh[]> meshMap, Vector2i startPos)  {
        try{
            ArrayList<Block> object = new ArrayList<>();
            String[] objectFile = Utils.loadResource("/game-objects/" + name + ".gameobj").split("\n");

            int width = 0;
            int height = 0;
            for (String line : objectFile) {
                if(line.startsWith("#") || line.isBlank()) {
                    continue;
                }
                String[] arguments = line.split(" ");
                if(arguments[0].equalsIgnoreCase("m")) {
                    checkLength(arguments.length, 3);
                    if(arguments[1].equalsIgnoreCase("width")) {
                        width = Integer.parseInt(arguments[2]);
                        checkLength(width, 1);
                    } else if(arguments[1].equalsIgnoreCase("height")) {
                        height = Integer.parseInt(arguments[2]);
                        checkLength(width, 1);
                    } else if(arguments[1].equalsIgnoreCase("clearArea") && arguments[2].equalsIgnoreCase("true")) {
                        for(int x = 0; x < width; x++) {
                            for(int y = 0; y < height; y++) {
                                Block block = new Block(null, false, new Vector2i(startPos.x + x, startPos.y + y));
                                object.add(block);
                            }
                        }
                    }
                } else if(arguments[0].equalsIgnoreCase("b")){
                    checkLength(arguments.length, 4);
                    String type = arguments[1];
                    int x = Integer.parseInt(arguments[2]);
                    int y = Integer.parseInt(arguments[3]);
                    Block block = new Block(meshMap.getOrDefault(type, null), false,
                            new Vector2i(startPos.x + x, startPos.y + y));
                    object.add(block);
                } else if(arguments[0].equalsIgnoreCase("ba")) {
                    checkLength(arguments.length, 6);
                    checkLength(arguments.length, 4);
                    String type = arguments[1];
                    int x1 = Integer.parseInt(arguments[2]);
                    int y1 = Integer.parseInt(arguments[3]);
                    int x2 = Integer.parseInt(arguments[4]);
                    int y2 = Integer.parseInt(arguments[5]);
                    for(int x = x1; x <= x2; x++) {
                        for(int y = y1; y <= y2; y++) {
                            Block block = new Block(meshMap.getOrDefault(type, null), false,
                                    new Vector2i(startPos.x + x, startPos.y + y));
                            object.add(block);
                        }
                    }
                }

            }
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static void checkLength(int amount, int min) {
        if(amount < min) {
            throw new RuntimeException("Wrong game object structure");
        }
    }
}

