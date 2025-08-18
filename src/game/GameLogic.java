package game;

import engine.graph.Material;
import game.world.MapManger;
import org.joml.*;

import static engine.Utils.eulerToQuaternion;
import static org.lwjgl.glfw.GLFW.*;
import engine.IGameLogic;
import engine.MouseInput;
import engine.Scene;
import engine.SceneLight;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Mesh;
import engine.graph.Renderer;
import engine.graph.anim.AnimGameItem;
import engine.graph.anim.Animation;
import engine.graph.lights.DirectionalLight;
import engine.graph.weather.Fog;
import engine.items.GameItem;
import engine.items.SkyBox;
import engine.loaders.assimp.AnimMeshesLoader;
import engine.loaders.assimp.StaticMeshesLoader;

import java.lang.Math;

public class GameLogic implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private Hud hud;

    private static final float CAMERA_POS_STEP = 0.40f;

    private Vector2f angleInc;

    private float lightAngle;

    private boolean firstTime;

    private boolean sceneChanged;

    private Animation animation;

    private AnimGameItem animItem;

    private MainPlayer playerItem;

    private MapManger mapManger;


    public GameLogic() {
        renderer = new Renderer();
        hud = new Hud();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = new Vector2f(0f, 0f);
        lightAngle = 90;
        firstTime = true;

    }

    @Override
    public void init(Window window) throws Exception {
        hud.init(window);
        renderer.init(window);

        scene = new Scene();

        camera.getPosition().x = 0f;
        camera.getPosition().y = 0f;
        camera.getPosition().z = 3.5f;
        camera.getRotation().x = 0.0f;
        camera.getRotation().y = 0.0f;

//        Mesh[] terrainMesh = StaticMeshesLoader.load("resources/models/terrain/terrain.obj", "models/terrain");
//        GameItem terrain = new GameItem(terrainMesh);
//        terrain.setScale(100.0f);
//
//        animItem = AnimMeshesLoader.loadAnimGameItem("resources/models/bob/boblamp.md5mesh", "");
//        animItem.setScale(0.05f);
//        animation = animItem.getCurrentAnimation();



//        scene.setGameItems(new GameItem[]{animItem, terrain});

        // Shadows
//        scene.setRenderShadows(true);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
//        scene.setFog(new Fog(true, fogColour, 0.02f));

        // Setup  SkyBox
        float skyBoxScale = 100.0f;
        SkyBox skyBox = new SkyBox("resources/models/examples/skybox.obj", new Vector4f(0.1f, 0.1f, 1.0f, 1.0f));
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();


        playerItem = new MainPlayer();

        mapManger = new MapManger(100, 30);
        mapManger.generateMap(scene);
        mapManger.putPlayerOnMapCenter(camera, playerItem);

        scene.setGameItems(new GameItem[] { playerItem});




    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(1f, 1f, 1f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);
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

//        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
//            sceneChanged = true;
//            if (animation != null) {
//                animation.nextFrame();
//            }
//        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {


        if(sceneChanged) {
            if(cameraInc.x != 0) {

            }
            camera.setRotation(cameraInc.y * 5f, cameraInc.x * 5f, 0f);
            System.out.println(cameraInc.y * 10f +" " + cameraInc.x * 10f);
            if(cameraInc.x == -1) {
                Quaternionf q = new Quaternionf(0.0f, 0.0f, 0.0f, 0.0f);
                playerItem.setRotation(q);

            } else if(cameraInc.x == 1) {
                Quaternionf q = new Quaternionf(0.0f, 1.0f, 0.0f, 0.00f);
                playerItem.setRotation(q);
            }
            Vector3f camPos = camera.getPosition();
            camera.setPosition(camPos.x + cameraInc.x*0.05f, camPos.y + cameraInc.y*0.05f, camPos.z + cameraInc.z*0.05f);

            Vector3f playerPos = playerItem.getPosition();
            playerItem.setPosition(playerPos.x + cameraInc.x*0.05f, playerPos.y + cameraInc.y*0.05f, playerPos.z );


        }



        //0.0 -1.0 0.0 0.2

        // Update camera position
//        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        /*
        lightAngle += angleInc;
        if (lightAngle < 0) {
            lightAngle = 0;
        } else if (lightAngle > 180) {
            lightAngle = 180;
        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();
*/
        // Update view matrix
        camera.updateViewMatrix();
    }

    @Override
    public void render(Window window) {
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }
        renderer.render(window, camera, scene, sceneChanged);
        hud.render(window);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();

        scene.cleanup();
    }
}
