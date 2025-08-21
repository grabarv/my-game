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
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 * Main player of the game
 */
public class MainPlayer extends GameItem {


    static final int heightInBlocks = 3;

    static final int widthInBlocks = 2;

    private float speed = 0f;


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
        setPosition(0, 0, MapManger.worldFirstZIndex + 0.01f);
        setScale(0.1f);
    }

    public void move(Vector2f movement) {
        if(movement.x == -1) {
            Quaternionf q = new Quaternionf(0.0f, 0.0f, 0.0f, 0.0f);
            setRotation(q);

        } else if(movement.x == 1) {
            Quaternionf q = new Quaternionf(0.0f, 1.0f, 0.0f, 0.00f);
            setRotation(q);
        }
        setPosition(getPosition().x + movement.x* speed, getPosition().y + movement.y*speed, getPosition().z );
    }



    /**
     * @param direction possible values: {@code up}, {@code down}, {@code left}, {@code right}.
     *                  If any other value is set, the method returns {@code false}.
     * @param map an instance of {@code MapManager}
     * @return {@code true} if it can move at least 1 px forward in the selected {@code direction},
     *         otherwise {@code false}.
     */
    public boolean canMove(String direction, MapManger map) {

        /* To check can player move in selected direction or not we have to find the nearest possible block positions in selected direction
            and then check if there are blocks.
            Also, we need to consider the fact that if a player is overlying with one of the side blocks and the overlying percent is small,
            it shouldn't interrupt player movement.
         */

        // Input check
        if(!Utils.isStringInArray(direction.toLowerCase(), new String[] {"up", "down", "left", "right"})) {
            return false;
        }


        Vector2i playerPosInBlockMap = getPlayerPosInBlockMap(map);

        Vector2f intersection = new Vector2f();

        // These variables represent side block presence which can influence the result. (As it was described earlier.)
        boolean firstElementResult = false;
        boolean lastElementResult = false;

        // Maximum possible intersection (in % of block size) for first and last elements
        float intersectionThreshold = 0.33f;

        int widthOffset = 0;
        int heightOffset = 0;
        String blockCorner = "";
        String playerCorner = "";



        if(direction.equalsIgnoreCase("right") || direction.equalsIgnoreCase("left")) {

            // Because we have defined player position in a block map for the top-left corner, we need to add offset to work with another sides
            if(direction.equalsIgnoreCase("right")) {
                widthOffset = widthInBlocks;
            }


            // Checking block presence for all blocks bordering with player in selected direction
            for(int i = 0; i < heightInBlocks+1; i++ ) {
                boolean result = map.isThereABlock(playerPosInBlockMap.x + widthOffset, (int) (playerPosInBlockMap.y + (float) i));
                if(result) {
                    if(i == 0) {
                        firstElementResult = true;
                    } else if(i == heightInBlocks) {
                        lastElementResult = true;
                    } else {
                        // Presence of block in the middle means player cannot move despite first and last elements presence/absence
                        return false;
                    }
                }
            }

            // In this case we only have to check up-down direction
            blockCorner = "downleft";
            playerCorner = "upleft";

            if(lastElementResult)  {
                heightOffset = heightInBlocks;
                String temp = blockCorner;
                blockCorner = playerCorner;
                playerCorner = temp;
            }
        }  else {

                // Same situation like with right direction
                if(direction.equalsIgnoreCase("down")) {
                    heightOffset = heightInBlocks;
                }

                // Almost the same as above
            for(int i = 0; i < widthInBlocks+1; i++ ) {
                boolean result = map.isThereABlock((int) (playerPosInBlockMap.x + (float) i),  (playerPosInBlockMap.y + heightOffset));
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

            // In this case we only have to check left-right direction
            blockCorner = "upright";
            playerCorner = "upleft";

            if(lastElementResult)  {
                widthOffset = widthInBlocks;
                String temp = blockCorner;
                blockCorner = playerCorner;
                playerCorner = temp;
            }

        }

        if(firstElementResult && lastElementResult) {
            return false;
        }
        if(!firstElementResult && !lastElementResult) {
            return true;
        }
        // Getting intersection percent
        if(map.checkBlockCoords(playerPosInBlockMap.x + widthOffset, playerPosInBlockMap.y + heightOffset)) {
            Block block = map.getBlocks()[playerPosInBlockMap.x + widthOffset][playerPosInBlockMap.y + heightOffset];
            intersection = getBlockIntersection(block, blockCorner, playerCorner);
        } else {
            return true;
        }

    // As mentioned above of up and down direction we need to check only x axis and vice versa
    if (direction.equalsIgnoreCase("down") || direction.equalsIgnoreCase("up")) {
            return Utils.isValueSmallerThen(intersection.x, intersectionThreshold);
        } else {
            return Utils.isValueSmallerThen(intersection.y, intersectionThreshold);
        }
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
     * @return a relative value (in % of block size) of intersection between a block corner and the OPPOSITE player corner.
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


        if(blockCorner.toLowerCase().startsWith("down")) {

            blockIntersection.y = (playerCornerPos.y - blockCornerPos.y) / block.getBlockSize().y;
        } else {

            blockIntersection.y = (blockCornerPos.y  - playerCornerPos.y) / block.getBlockSize().y;
        }

        return new Vector2f(blockIntersection.x, blockIntersection.y);
    }



    /*public Vector3f getPlayerTopLeftCorner() {
        return new Vector3f(getPosition().x - modelWidth * getScale() /2f, getPosition().y + modelHeight * getScale() /2f, getPosition().z);
    }*/

    public Vector2f getPlayerSize() {
        return new Vector2f(modelWidth * getScale(), modelHeight * getScale());
    }

    public void setSpeed(float newSpeed) {
        speed = newSpeed;
        if(speed < 0f) {
            speed = 0f;
        }
    }

    public float getSpeed() {
        return speed;
    }
}


