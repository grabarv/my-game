package game;

import engine.Utils;
import engine.Window;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.assimp.StaticMeshesLoader;
import game.world.Block;
import game.world.MapManger;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;

/**
 * Main player of the game
 */
public class MainPlayer extends GameItem {


    static final int heightInBlocks = 3;

    static final int widthInBlocks = 2;


    public MainPlayer() throws Exception {
        super(false);
        modelHeight = 2f;
        modelWidth = 1.33332f;
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
     * @param direction possible values: {@code up}, {@code down}, {@code left}, {@code right}.
     *                  If any other value is set, the method returns {@code false}.
     * @param map an instance of {@code MapManager}
     * @return {@code true} if it can move at least 1 px forward in the selected {@code direction},
     *         otherwise {@code false}.
     */
    public boolean canMove(String direction, MapManger map) {
        if(!Utils.isStringInArray(direction.toLowerCase(), new String[] {"up", "down", "left", "right"})) {
            return false;
        }
        Vector2i playerPosInBlockMap = getPlayerPosInBlockMap(map);

        Vector2f intersection = new Vector2f();

        boolean firstElementResult = false;
        boolean lastElementResult = false;


        if (direction.equalsIgnoreCase("left")) {
            for(int i = 0; i < heightInBlocks+1; i++ ) {
                boolean result = map.isThereABlock(playerPosInBlockMap.x , (int) (playerPosInBlockMap.y + (float) i));
                if(result) {
                    if(i == 0) {
                        firstElementResult = true;
                    } else if(i == heightInBlocks) {
                        lastElementResult = true;
                    } else {
                        return false;
                    }
                }
            }
            if(firstElementResult && lastElementResult) {
                return false;
            }
            if(!firstElementResult && !lastElementResult) {
                return true;
            }
            if(firstElementResult) {
                if(map.checkBlockCoords(playerPosInBlockMap.x, playerPosInBlockMap.y)) {
                    Block block = map.getBlocks()[playerPosInBlockMap.x][playerPosInBlockMap.y];
                    intersection = getBlockIntersection(block, "downleft", "upleft");
                } else {
                    return true;
                }

            } else {
                if(map.checkBlockCoords(playerPosInBlockMap.x, playerPosInBlockMap.y + heightInBlocks)) {
                    Block block = map.getBlocks()[playerPosInBlockMap.x][playerPosInBlockMap.y + heightInBlocks];
                    intersection = getBlockIntersection(block, "upleft", "downleft");
                } else {
                    return true;
                }
            }

            if(intersection.y < 0.1) {
                return true;
            } else {
                return false;
            }
        } else if(direction.equalsIgnoreCase("right")) {

            for(int i = 0; i < heightInBlocks+1; i++ ) {
                boolean result = map.isThereABlock(playerPosInBlockMap.x + widthInBlocks, (int) (playerPosInBlockMap.y + (float) i));
                if(result) {
                    if(i == 0) {
                        firstElementResult = true;
                    } else if(i == heightInBlocks) {
                        lastElementResult = true;
                    } else {
                        return false;
                    }
                }
            }
            if(firstElementResult && lastElementResult) {
                return false;
            }
            if(!firstElementResult && !lastElementResult) {
                return true;
            }
            if(firstElementResult) {
                if(map.checkBlockCoords(playerPosInBlockMap.x + widthInBlocks, playerPosInBlockMap.y)) {
                    Block block = map.getBlocks()[playerPosInBlockMap.x + widthInBlocks][playerPosInBlockMap.y];
                    intersection = getBlockIntersection(block, "downleft", "upleft");
                } else {
                    return true;
                }

            } else {
                if(map.checkBlockCoords(playerPosInBlockMap.x + widthInBlocks, playerPosInBlockMap.y + heightInBlocks)) {
                    Block block = map.getBlocks()[playerPosInBlockMap.x + widthInBlocks][playerPosInBlockMap.y + heightInBlocks];
                    intersection = getBlockIntersection(block, "upleft", "downleft");
                } else {
                    return true;
                }
            }

            if(intersection.y < 0.1) {
                return true;
            } else {
                return false;
            }

        } else if(direction.equalsIgnoreCase("up")) {
            for(int i = 0; i < widthInBlocks+1; i++ ) {
                boolean result = map.isThereABlock((int) (playerPosInBlockMap.x + (float) i),  (playerPosInBlockMap.y ));
                if(result) {
                    if(i == 0) {
                        firstElementResult = true;
                    } else if(i == widthInBlocks) {
                        lastElementResult = true;
                    } else {
                        return false;
                    }
                }
            }
            if(firstElementResult && lastElementResult) {
                return false;
            }
            if(!firstElementResult && !lastElementResult) {
                return true;
            }
            if(firstElementResult) {
                if(map.checkBlockCoords(playerPosInBlockMap.x, playerPosInBlockMap.y)) {
                    Block block = map.getBlocks()[playerPosInBlockMap.x][playerPosInBlockMap.y];
                    intersection = getBlockIntersection(block, "upright", "upleft");
                } else {
                    return true;
                }

            } else {
                if(map.checkBlockCoords(playerPosInBlockMap.x + widthInBlocks, playerPosInBlockMap.y)) {
                    Block block = map.getBlocks()[playerPosInBlockMap.x + widthInBlocks][playerPosInBlockMap.y];
                    intersection = getBlockIntersection(block, "upleft", "upright");
                } else {
                    return true;
                }
            }

            if(intersection.x < 0.1) {
                return true;
            } else {
                return false;
            }

        } else if(direction.equalsIgnoreCase("down")) {

            for(int i = 0; i < widthInBlocks+1; i++ ) {
                boolean result = map.isThereABlock((int) (playerPosInBlockMap.x + (float) i),  (playerPosInBlockMap.y + heightInBlocks));
                if(result) {
                    if(i == 0) {
                        firstElementResult = true;
                    } else if(i == widthInBlocks) {
                        lastElementResult = true;
                    } else {
                        return false;
                    }
                }
            }
            if(firstElementResult && lastElementResult) {
                return false;
            }

            if(!firstElementResult && !lastElementResult) {
                return true;
            }

            if(firstElementResult) {
                if(map.checkBlockCoords(playerPosInBlockMap.x, playerPosInBlockMap.y + heightInBlocks)) {
                    Block block = map.getBlocks()[playerPosInBlockMap.x][playerPosInBlockMap.y + heightInBlocks];
                    intersection = getBlockIntersection(block, "upright", "upleft");
                } else {
                    return true;
                }

            } else {
                if(map.checkBlockCoords(playerPosInBlockMap.x + widthInBlocks, playerPosInBlockMap.y + heightInBlocks)) {
                    Block block = map.getBlocks()[playerPosInBlockMap.x + widthInBlocks][playerPosInBlockMap.y + heightInBlocks];
                    intersection = getBlockIntersection(block, "upleft", "upright");
                } else {
                    return true;
                }
            }

            if(intersection.x < 0.1) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private Vector2i getPlayerPosInBlockMap(MapManger map) {
        Vector2f playerTopLeftCorner = new Vector2f(getPosition().x - modelWidth * getScale()/2, getPosition().y +modelHeight * getScale()/2);
        Vector3f mapTopLeftCorner = map.getMapTopLeftCorner();
        Vector2f posDiff = new Vector2f(playerTopLeftCorner.x - mapTopLeftCorner.x, mapTopLeftCorner.y - playerTopLeftCorner.y);
        Vector2i playerInBlockMapPos = new Vector2i((int) Math.floor(posDiff.x / map.getBlocks()[0][0].getBlockSize().x),
                (int) Math.floor(posDiff.y / map.getBlocks()[0][0].getBlockSize().y));
        return playerInBlockMapPos;
    }

    public void setPlayerInMapTopLeftCorner(MapManger map) {
        Vector3f mapTopLeftCorner = map.getMapTopLeftCorner();
        setPosition(mapTopLeftCorner.x + modelWidth * getScale()/2, mapTopLeftCorner.y - modelHeight * getScale()/2, getPosition().z);
    }


    /**
     * @param block a block whose intersection we want to check
     * @param blockCorner possible values: {@code upleft}, {@code upright}, {@code downleft}, {@code downright}
     * @param playerCorner possible values: {@code upleft}, {@code upright}, {@code downleft}, {@code downright}
     * @return a relative value of intersection between a block corner and the OPPOSITE player corner.
     *         <p>Examples:</p>
     *         <ul>
     *           <li>Returns {@code (0.0f, 0.0f)} if {@code blockCorner} = {@code playerCorner}.</li>
     *           <li>Returns {@code (0.5f, 0.5f)} if {@code playerCorner} is in the middle of a block.</li>
     *           <li>Returns {@code (1.0f, 1.0f)} if there is 100% intersection between elements.</li>
     *         </ul>
     */
    public Vector2f getBlockIntersection(Block block, String blockCorner, String playerCorner) {
        Vector2f blockIntersection = new Vector2f(0f, 0f);

        // Input check: blockCorner
        if(!Utils.isStringInArray(blockCorner.toLowerCase(), new String[]{"upleft", "upright", "downleft", "downright"})) {
            return blockIntersection;
        }

        //Input check: playerCorner
        if(!Utils.isStringInArray(playerCorner.toLowerCase(), new String[]{"upleft", "upright", "downleft", "downright"})) {
            return blockIntersection;
        }

        Vector3f blockCornerPos = block.getCorner(blockCorner);

        Vector3f playerCornerPos = getCorner(playerCorner);

        if(blockCorner.toLowerCase().endsWith("right")) {
            blockIntersection.x = (blockCornerPos.x - playerCornerPos.x) / block.getBlockSize().x;
        } else {
            blockIntersection.x = (playerCornerPos.x - blockCornerPos.x) / block.getBlockSize().x;
        }

//        if(blockIntersection.x < 0f) {
//            blockIntersection.x = 0f;
//        }
//
//        if (blockIntersection.x > 1f) {
//            blockIntersection.x = 1f;
//        }

        if(blockCorner.toLowerCase().startsWith("down")) {

            blockIntersection.y = (playerCornerPos.y - blockCornerPos.y) / block.getBlockSize().y;
        } else {

            blockIntersection.y = (blockCornerPos.y  - playerCornerPos.y) / block.getBlockSize().y;
        }

//        if(blockIntersection.y < 0f) {
//            blockIntersection.y = 0f;
//        }
//
//        if (blockIntersection.y > 1f) {
//            blockIntersection.y = 1f;
//        }

        return new Vector2f(blockIntersection.x, blockIntersection.y);
    }



    /*public Vector3f getPlayerTopLeftCorner() {
        return new Vector3f(getPosition().x - modelWidth * getScale() /2f, getPosition().y + modelHeight * getScale() /2f, getPosition().z);
    }*/

    public Vector2f getPlayerSize() {
        return new Vector2f(modelWidth * getScale(), modelHeight * getScale());
    }



}


