package game;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.assimp.StaticMeshesLoader;
import game.world.MapManger;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Arrays;

/**
 * Main player of the game
 */
public class MainPlayer extends GameItem {

    private final String texturePath = "resources/textures/player.png";

    private final String modelPath = "resources/models/main_player.obj";

    private final float modelHeight = 1f;
    private final float modelWidth = 0.66f;

    public MainPlayer() throws Exception {
        super(false);
        Mesh playerMesh = StaticMeshesLoader.load(modelPath, "", 0)[0];
        Texture texture = new Texture(texturePath);
        Material material = new Material(texture, 0);
        playerMesh.setMaterial(material);
        setMesh(playerMesh);
        setPosition(0, 0, 1.5f + 0.33f);
        setScale(0.1f);

    }

    /**
     * @param direction possible values: up,down,left,right. If any other value is set, returns false
     * @param map just MapManager class
     * @return true if it can move at least 1 px forward in the selected direction
     */
    public boolean canMove(String direction, MapManger map) {
        if (!Arrays.asList(new String[] {"up", "down", "left", "right"}).contains(direction)) {
            System.out.println("-----------");
            return false;
        }
        Vector2f playerTopLeftCorner = new Vector2f(getPosition().x - modelWidth * getScale()/2, getPosition().y +modelHeight * getScale()/2);
        Vector2f positionDifference = new Vector2f(playerTopLeftCorner.x - map.getStartPos().x, map.getStartPos().y -  playerTopLeftCorner.y);
//        System.out.println(getPosition().x + " " + getPosition().y);
//        System.out.println(playerTopLeftCorner.x + " " + playerTopLeftCorner.y + " ");
        setPosition(map.getStartPos().x + modelWidth * getScale()/2, playerTopLeftCorner.y - modelHeight * getScale()/2, getPosition().z);
        Vector2i playerInBlockMapPos = new Vector2i((int) Math.floor(positionDifference.x / map.getBlockSize().x),
                (int) Math.floor(positionDifference.y / map.getBlockSize().y));

        if (direction.equalsIgnoreCase("left")) {
//            System.out.println(playerInBlockMapPos.x + " " + playerInBlockMapPos.y);

            for(int i = 0; i < 3; i++ ) {
                if(map.isThereABlock(playerInBlockMapPos.x-1, playerInBlockMapPos.y +i)) {
                    return false;
                }
            }
        }

        System.out.println(positionDifference.x + " " + positionDifference.y);
        return true;
    }


}


