package game;

import engine.graph.*;
import game.world.MainPlayer;
import game.world.map.MapItem;
import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;
import engine.IGameLogic;
import engine.MouseInput;
import engine.Scene;
import engine.Window;
import engine.graph.anim.AnimGameItem;
import engine.graph.anim.Animation;

public class ProgramLogic implements IGameLogic {

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private Hud hud;

//    private Vector2f angleInc;

    private float lightAngle;

    private boolean firstTime;

    private boolean sceneChanged;

    private Animation animation;

    private AnimGameItem animItem;


    public static boolean testVar;

    private float angleInc;

    private boolean gameLoaded = false;
    private GameManager gameLoader;



    public ProgramLogic() {
        renderer = new Renderer();
        hud = new Hud();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 90;
        firstTime = true;

    }

    @Override
    public void init(Window window) throws Exception {
        hud.init(window, new String[]{"resources/textures/map_objects/struct_grass.png", "", "", "", "", "","","", "", ""});
        renderer.init(window);

        scene = new Scene();

        gameLoader = new GameManager(scene, camera);

        // gameLoader.load();


//        angleInc = new Vector2f(0f,0f);

    //        Mesh[] terrainMesh = StaticMeshesLoader.loadGameObject("resources/models/terrain/terrain.obj",
    // "models/terrain");
    //        GameItem terrain = new GameItem(terrainMesh);
    //        terrain.setScale(100.0f);
    //
    //        animItem = AnimMeshesLoader.loadAnimGameItem("resources/models/bob/boblamp.md5mesh",
    // "");
    //        animItem.setScale(0.05f);
    //        animation = animItem.getCurrentAnimation();

    //        scene.setGameItems(new GameItem[]{animItem, terrain});

    // Shadows


//         Map<Mesh, List<GameItem>> map= scene.getGameMeshes();
//
//        for (Map.Entry<Mesh, List<GameItem>> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " => " + entry.getValue().size());
//        }
//
//        System.out.println("-------------");
//
//        Map<InstancedMesh, List<GameItem>> map2 = scene.getGameInstancedMeshes();
//
//        for (Map.Entry<InstancedMesh, List<GameItem>> entry : map2.entrySet()) {
//            System.out.println(entry.getKey() + " => " + entry.getValue().size());
//        }
    }


    @Override
    public void input(Window window, MouseInput mouseInput) {

        sceneChanged = false;
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            sceneChanged = true;
            cameraInc.y = 1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            sceneChanged = true;
            cameraInc.y = -1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            sceneChanged = true;
            cameraInc.x = -1;

        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            sceneChanged = true;
            cameraInc.x = 1;
        }
        if(window.isKeyPressed(GLFW_KEY_N)) {
            sceneChanged = true;
            cameraInc.z = 1;

        } else if(window.isKeyPressed(GLFW_KEY_M)) {
            sceneChanged = true;
            cameraInc.z = -1;
        }


        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            sceneChanged = true;
            testVar = true;
            MainPlayer.DEBUG_MODE = true;
        }

        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            sceneChanged = true;
            angleInc -= 0.05f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            sceneChanged = true;
            angleInc += 0.05f;
        } else {
            angleInc = 0;
        }
        //-0.7701382 0.04165437 0.6365155

    }

    @Override
    public boolean update(float interval, MouseInput mouseInput, Window window) {
        if(hud.getHudResult().startGame) {
            gameLoader.loadNewGame();
            hud.getHudResult().startGame = false;
            gameLoaded = true;
            sceneChanged = true;
        } else if(hud.getHudResult().loadGame) {
            gameLoader.loadSavedGame();
            hud.getHudResult().loadGame = false;
            gameLoaded = true;
            sceneChanged = true;
        } else if (hud.getHudResult().exitGame) {
            return false;
        }

        if(gameLoaded) {
            if (sceneChanged) {
                gameLoader.updateGameState(cameraInc, angleInc, mouseInput);
            }
//        gameLoader.updateGameState(cameraInc, angleInc, mouseInput);
            camera.updateViewMatrix();
        }


        return true;
    }

    @Override
    public void render(Window window, MouseInput mouseInput) {
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }
        renderer.render(window, camera, scene, sceneChanged);
        hud.startRender(window);
        if(!gameLoaded) {
            hud.renderStartWindow(window, mouseInput);
        } else {
            hud.renderPlayerInventory(window, new String[]{"struct_grass", "", "", "", "", "","","", "", ""});
            hud.renderHeath(window, 50, 100);
            hud.renderStamina(window, 50, 200);

            if(hud.getHudResult().openSettingsInGame) {
                hud.renderSettingsMenu(window, mouseInput);
            } else  {
                try {
                    hud.renderSettingsButton(window, mouseInput);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        hud.endRender(window);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
    }

}
