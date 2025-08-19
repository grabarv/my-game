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

    private final float modelHeight = 2f;
    private final float modelWidth = 1.33332f;

    public MainPlayer() throws Exception {
        super(false);
        final String modelPath = "resources/models/main_player.obj";
        Mesh playerMesh = StaticMeshesLoader.load(modelPath, "", 0)[0];
        final String texturePath = "resources/textures/player.png";
        Texture texture = new Texture(texturePath);
        Material material = new Material(texture, 0);
        playerMesh.setMaterial(material);
        setMesh(playerMesh);
        setPosition(0, 0, MapManger.worldFirstZIndex);
        setScale(0.1f);

    }

    /**
     * @param direction possible values: up,down,left,right. If any other value is set, returns false
     * @param map just MapManager class
     * @return Return true if it can move at least 1 px forward in the selected direction, otherwise return false
     */
    // TODO: consider possible deviation
    public boolean canMove(String direction, MapManger map) {
        if (!Arrays.asList(new String[] {"up", "down", "left", "right"}).contains(direction)) {
            return false;
        }
        Vector2i playerPosInBlockMap = getPlayerPosInBlockMap(map);

        if (direction.equalsIgnoreCase("left")) {
            for(int i = 0; i < 4; i++ ) {
                if(map.isThereABlock(playerPosInBlockMap.x, (int) (playerPosInBlockMap.y + (float) i))) {
                    return false;
                }
            }
        } else if(direction.equalsIgnoreCase("right")) {
            for(int i = 0; i < 4; i++ ) {
                if(map.isThereABlock(playerPosInBlockMap.x + 2, (int) (playerPosInBlockMap.y + (float) i))) {
                    return false;
                }
            }

        } else if(direction.equalsIgnoreCase("up")) {
            for(int i = 0; i < 3; i++ ) {
                if(map.isThereABlock((int) (playerPosInBlockMap.x + (float) i),  (playerPosInBlockMap.y ))) {
                    return false;
                }
            }
        } else if(direction.equalsIgnoreCase("down")) {
            for(int i = 0; i < 3; i++ ) {
                if(map.isThereABlock((int) (playerPosInBlockMap.x + (float) i),  (playerPosInBlockMap.y + 3 ))) {
                    return false;
                }
            }
        }

        return true;
    }

    private Vector2i getPlayerPosInBlockMap(MapManger map) {
        Vector2f playerTopLeftCorner = new Vector2f(getPosition().x - modelWidth * getScale()/2, getPosition().y +modelHeight * getScale()/2);
        Vector3f mapTopLeftCorner = map.getMapTopLeftCorner();
        Vector2f posDiff = new Vector2f(playerTopLeftCorner.x - mapTopLeftCorner.x, mapTopLeftCorner.y - playerTopLeftCorner.y);
        Vector2i playerInBlockMapPos = new Vector2i((int) Math.floor(posDiff.x / map.getBlockSize().x),
                (int) Math.floor(posDiff.y / map.getBlockSize().y));
        return playerInBlockMapPos;
    }

    public void setPlayerInMapTopLeftCorner(MapManger map) {
        Vector3f mapTopLeftCorner = map.getMapTopLeftCorner();
        setPosition(mapTopLeftCorner.x + modelWidth * getScale()/2, mapTopLeftCorner.y - modelHeight * getScale()/2, getPosition().z);
    }





}


