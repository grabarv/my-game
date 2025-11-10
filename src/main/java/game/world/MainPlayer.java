package game.world;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import game.world.map.Block;
import game.world.map.ShapeType;
import game.world.map.MapManager;
import game.world.map.Structure;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import utils.GeometryUtils;
import utils.Utils;
import utils.loaders.assimp.StaticMeshesLoader;

import static game.world.map.MapManager.worldBlockZIndex;

/**
 * Main player of the game
 */
public class MainPlayer extends GameItem {

    public static final float MOVEMENT_STEP = 0.001f;
    static final int heightInBlocks = 3;

    static final int widthInBlocks = 2;

    private float speed = 0f;

    public static boolean DEBUG_MODE = false;

    /**
    * Player has 3 modes:
    * <ul>
    *     <li>walking - where gravity works and player can only jump instead of flying</li>
    *     <li>flying - where gravity for player doesn't work and player can fly</li>
    *     <li>spirit mode - same as flying but player can move throw blocks</li>
    * </ul>
    */
    private MoveMode moveMode = MoveMode.WALKING;

    private boolean isJumping = false;

    /**
     * Is needed for controlling that jumping key was released before the next jump
     */
    private boolean isReadyForJump = false;

    private float jumpPower = 0.0f;

    private float jumpPowerUsage = 1f/1000f;

    private float jumpSpeed;

    private final MapManager map;


    public MainPlayer(MapManager map) {
        super(false);
        this.map = map;
        modelHeight = 2.0000f;
        modelWidth = 1.333333f;
        final String modelPath = "resources/models/main_player.obj";
        Mesh playerMesh = null;
        try {
            playerMesh = StaticMeshesLoader.load(modelPath, "", 0)[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        final String texturePath = "resources/textures/player.png";
        Texture texture = null;
        try {
            texture = new Texture(texturePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Material material = new Material(texture, 0);
        playerMesh.setMaterial(material);
        setMesh(playerMesh);
        setPosition(0, 0, worldBlockZIndex + 0.01f);
        setScale(0.09f);
        setRotation(new Quaternionf(0.0f, 0f, 0f, 0f));
    }

    // TODO: Fix bug with moving near trinagle block in house (player stucks now in it)
    public Vector2f move(Vector2f movement) {

        // rotating player depending on his direction
        if(movement.x == -1) {
            getRotation().y = 0.0f;

        } else if(movement.x == 1) {
            getRotation().y = 1.0f;
        }
        if(moveMode == MoveMode.SPIRIT) {
            setPosition(getPosition().x + movement.x* MOVEMENT_STEP, getPosition().y + movement.y* MOVEMENT_STEP, getPosition().z );
            return movement;
        } else if(moveMode == MoveMode.FLYING) {
            return moveToPossiblePosition(movement);
        }
        if(moveMode == MoveMode.WALKING) {

            boolean canMoveDown = isPlayerPositionPossible(map, new Vector2f(getPosition().x, getPosition().y - MOVEMENT_STEP));


            if(movement.y != 1 && !isJumping && !canMoveDown) {
                isReadyForJump = true;
            }

            if(canMoveDown && !isJumping) {
                movement.y = -1f;
            }
            if(!canMoveDown && !isJumping && !isReadyForJump) {
                movement.y = 0f;
            }
            if (!canMoveDown && !isJumping && movement.y == 1.0f) {
                isJumping = true;
                jumpPower = 1.0f;
            }
            if(isJumping) {
                if(movement.y != 1f) {
                    movement.y = 0f;
                    isJumping = false;
                    isReadyForJump = false;
                }
//                movement.y = 1f;
                jumpPower -= jumpPowerUsage;
            }
            if(jumpPower <= 0.0f && isJumping) {
                isJumping = false;
                isReadyForJump = false;
            }
            return moveToPossiblePosition(movement);
        } else {
            setPosition(getPosition().x + movement.x* MOVEMENT_STEP, getPosition().y + movement.y* MOVEMENT_STEP, getPosition().z );
            return movement;
        }
    }

    private Vector2f moveToPossiblePosition(Vector2f movement) {
        if(movement.x == 0 && movement.y == 0) {
            return movement;
        }
        Vector2f realMovement = new Vector2f(movement.x, movement.y);
        if(movement.y != 0) {
            float nextYPos = getPosition().y + movement.y* MOVEMENT_STEP;

            if(isPlayerPositionPossible(map, new Vector2f(getPosition().x, nextYPos))) {
                realMovement.y = movement.y;
            } else if(movement.x == 0f && isPlayerPositionPossible(map, new Vector2f(getPosition().x + MOVEMENT_STEP, nextYPos))) {
                realMovement.y = movement.y;
                realMovement.x = 1f;
            } else if(movement.x == 0f && isPlayerPositionPossible(map, new Vector2f(getPosition().x - MOVEMENT_STEP, nextYPos))) {
                realMovement.y = movement.y;
                realMovement.x = -1f;
            } else  {
                realMovement.y = 0f;
            }
        }

        if(movement.x != 0) {
            float nextXPos = getPosition().x + movement.x* MOVEMENT_STEP;
            if(isPlayerPositionPossible(map, new Vector2f(nextXPos, getPosition().y))) {
                realMovement.x = movement.x;
            } else if(movement.y == 0f && isPlayerPositionPossible(map, new Vector2f(nextXPos, getPosition().y + MOVEMENT_STEP))) {
                realMovement.x = movement.x;
                realMovement.y = 1f;
            } else if(movement.y == 0f && isPlayerPositionPossible(map, new Vector2f(nextXPos, getPosition().y - MOVEMENT_STEP))) {
                realMovement.x = movement.x;
                realMovement.y = -1f;
            } else  {
                realMovement.x = 0f;
            }
        }

        getPosition().x += realMovement.x * MOVEMENT_STEP;
        getPosition().y += realMovement.y * MOVEMENT_STEP;
        return realMovement;
    }



    /**
     * @param direction possible values: {@code up}, {@code down}, {@code left}, {@code right}.
     *                  If any other value is set, the method returns {@code false}.
     * @return {@code true} if it can move at least 1 px forward in the selected {@code direction},
     *         otherwise {@code false}.
     */
    public boolean canMove(String direction) {

        return true;
            /* To check can player move in selected direction or not we have to find the nearest possible block positions in selected direction
                and then check if there are blocks.
                Also, we need to consider the fact that if a player is overlying with one of the side blocks and the overlying percent is small,
                it shouldn't interrupt player movement.
             */

        // Input check
           /* if(!Utils.isStringInArray(direction.toLowerCase(), new String[] {"up", "down", "left", "right"})) {
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
            }*/
    }

    private Vector2i getPlayerPosInBlockMap(MapManager map) {
        Vector2f playerTopLeftCorner = getPlayerTopLeftCorner();
        Vector2f mapTopLeftCorner = map.getMapTopLeftCorner();
        Vector2f posDiff = new Vector2f(playerTopLeftCorner.x - mapTopLeftCorner.x, mapTopLeftCorner.y - playerTopLeftCorner.y);
        Vector2i playerInBlockMapPos = new Vector2i((int) Math.floor(posDiff.x / map.getBlocks()[0][0].getSize().x),
                (int) Math.floor(posDiff.y / map.getBlocks()[0][0].getSize().y));
        return playerInBlockMapPos;
    }
    private Vector2f getPlayerTopLeftCorner() {
        return new Vector2f(getPosition().x - modelWidth * getScale() /2f, getPosition().y + modelHeight * getScale() /2f);
    }
    /**
     * Sets player position to the top-left corner of the map.
     * <p>Note: Player position is set in a way that his top-left corner coincides with the map top-left corner.</p>
     * @param map MapManger object representing the map
     */

    public void setPlayerInMapTopLeftCorner(MapManager map) {
        Vector2f mapTopLeftCorner = map.getMapTopLeftCorner();
        setPosition(mapTopLeftCorner.x + modelWidth * getScale()/2, mapTopLeftCorner.y - modelHeight * getScale()/2, getPosition().z);
    }

    public boolean isPlayerPositionPossible(MapManager map, Vector2f nextPosition) {
        Vector2i playerPosInBlockMap = getPlayerPosInBlockMap(map);
        // Checking blocks presence in the center of the player
        for(int i = -1; i <= widthInBlocks + 1; i++) {
            for(int j = -1; j <= heightInBlocks + 1; j++) {
                Block block = map.getBlocks()[playerPosInBlockMap.x + i][playerPosInBlockMap.y + j];
                if(block == null || !block.getIsInScene()) {
                    continue;
                }
                if(block.getShapeType() == ShapeType.RECTANGLE) {
                    if(GeometryUtils.intersects(new GeometryUtils.Rectangle(block.get2DPosition(), Block.get2DSize()),
                            new GeometryUtils.Rectangle(nextPosition, getPlayerSize()))) {
                        return false;
                    }
                } else if(block.getShapeType() == ShapeType.TRIANGLE) {
                    if(GeometryUtils.intersects(new GeometryUtils.Triangle(block.getShapeVertices().get(0), block.getShapeVertices().get(1), block.getShapeVertices().get(2)),
                            new GeometryUtils.Rectangle(nextPosition, getPlayerSize()))) {
                        return false;
                    }
                }
            }
        }

        for(Structure structure : map.getStructures()) {
            Vector2i structurePosInBlockMap = structure.getMapPosition();
            Vector2i structureSizeInBlocks = structure.getSizeInBlocks();
            if(!structure.getIsInScene() || structure.canMoveThrow()) {
                continue;
            }
            if(structure.getShapeType() == ShapeType.RECTANGLE) {
                if(GeometryUtils.intersects(new GeometryUtils.Rectangle(new Vector2f(structure.getPosition().x /*- structure.getSize().x/2*/,
                                structure.getPosition().y /*+ structure.getSize().y/2*/),
                        new Vector2f(structureSizeInBlocks.x * Block.get2DSize().x, structureSizeInBlocks.y * Block.get2DSize().y)),
                        new GeometryUtils.Rectangle(nextPosition, getPlayerSize()))) {
                    return false;
                }
            } else if(structure.getShapeType() == ShapeType.TRIANGLE) {
                if(GeometryUtils.intersects(new GeometryUtils.Triangle(structure.getShapeVertices().get(0), structure.getShapeVertices().get(1), structure.getShapeVertices().get(2)),
                        new GeometryUtils.Rectangle(nextPosition, getPlayerSize()))) {
                    return false;
                }
            }
        }
        return true;
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
            blockIntersection.x = (blockCornerPos.x - playerCornerPos.x) / block.getSize().x;
        } else {
            blockIntersection.x = (playerCornerPos.x - blockCornerPos.x) / block.getSize().x;
        }


        if(blockCorner.toLowerCase().startsWith("down")) {

            blockIntersection.y = (playerCornerPos.y - blockCornerPos.y) / block.getSize().y;
        } else {

            blockIntersection.y = (blockCornerPos.y  - playerCornerPos.y) / block.getSize().y;
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
        jumpSpeed = speed*2f;
        if(speed < 0f) {
            speed = 0f;
            jumpSpeed = 0f;
        }
    }

    public float getSpeed() {
        return speed;
    }

    public void setMoveMode(MoveMode moveMode) {
        this.moveMode = moveMode;
        if(moveMode == MoveMode.WALKING) {
            isJumping = false;
            isReadyForJump = false;
        }
    }

    public MoveMode getMoveMode() {
        return moveMode;
    }

    public enum MoveMode {
        WALKING,
        FLYING,
        SPIRIT
    }
}