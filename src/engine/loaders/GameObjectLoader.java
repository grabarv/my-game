package engine.loaders;

import engine.Utils;
import engine.graph.Mesh;
import game.world.Block;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;

public class GameObjectLoader {

    public static Block[] load(String path, Map<String, Mesh[]> meshMap, Vector2i startPos)  {
        try{
            String object = Utils.readAllLines(path);
            while (object)
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
