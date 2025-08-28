package game;

import engine.graph.*;
import engine.graph.lights.PointLight;
import engine.graph.lights.SpotLight;
import engine.graph.weather.Fog;
import game.world.MapManger;
import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;
import engine.IGameLogic;
import engine.MouseInput;
import engine.Scene;
import engine.SceneLight;
import engine.Window;
import engine.graph.anim.AnimGameItem;
import engine.graph.anim.Animation;
import engine.graph.lights.DirectionalLight;
import engine.items.GameItem;
import engine.items.SkyBox;

import java.lang.Math;

public class GameLogic implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

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

    private MainPlayer playerItem;

    private MapManger mapManger;

    public static boolean testVar;

    private float angleInc;


    public GameLogic() {
        renderer = new Renderer();
        hud = new Hud();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
    //        angleInc = new Vector2f(0f, 0f);
        angleInc = 0;
        lightAngle = 90;
    firstTime = true;

    }

    @Override
    public void init(Window window) throws Exception {
        hud.init(window);
        renderer.init(window);


        camera.getPosition().x = 0f;
        camera.getPosition().y = 0f;
        camera.getPosition().z = 4f;
        camera.getRotation().x = 0.0f;
        camera.getRotation().y = 0.0f;
        camera.getRotation().z = 0f;

        scene = new Scene();
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
            scene.setRenderShadows(true);

    // Fog
    Vector3f fogColour = new Vector3f(0.3f, 0.3f, 0.3f);
//        scene.setFog(new Fog(true, fogColour, 0.2f));

        // Setup  SkyBox
        float skyBoxScale = 500f;
        SkyBox skyBox = new SkyBox("resources/models/examples/skybox.obj", "resources/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

        mapManger = new MapManger(1000, 100);
        mapManger.generateMap(scene);


        playerItem = new MainPlayer(mapManger);
        playerItem.setSpeed(0.05f); // 0.01f


        mapManger.putPlayerOnMapCenter(camera, playerItem);

        scene.setGameItems(new GameItem[] { playerItem});

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

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.0f, 0.0f, 0.0f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 5.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);


        // Point Light
        /*Vector3f lightPosition = new Vector3f(31.6f, -1.8f, 2);
        lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        sceneLight.setPointLightList(new PointLight[]{pointLight});
*/
        // Spot Light
  /*      lightPosition = new Vector3f(31.6f, -1.8f, 2f);
        pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        att = new PointLight.Attenuation(0.0f, 0.0f, 0.02f);
        pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0, 0, -1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
        sceneLight.setSpotLightList(new SpotLight[]{spotLight, new SpotLight(spotLight)});
  */  }

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
    public void update(float interval, MouseInput mouseInput, Window window) {

        // For debugging
        if (mouseInput.isRightButtonPressed()) {
            // Update camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            sceneChanged = true;
        }
//        System.out.println("--------");
//        System.out.println(camera.getPosition().x + " " + camera.getPosition().y + " " + camera.getPosition().z);

        sceneChanged = true;

        checkCameraInc();

        Vector2f realMovement = playerItem.move(new Vector2f(cameraInc.x, cameraInc.y));

        cameraInc.set(realMovement, cameraInc.z);

//        animateCameraRotation(0.25f, 2.5f);

        camera.setPosition(playerItem.getPosition().x, playerItem.getPosition().y, camera.getPosition().z + cameraInc.z * 0.05f);

//        if(sceneChanged) {
//            checkCameraInc();
//
//            playerItem.move(new Vector2f(cameraInc.x, cameraInc.y));
//
//            camera.setPosition(playerItem.getPosition().x, playerItem.getPosition().y, camera.getPosition().z);
//
//        }

        lightAngle += angleInc;
        if (lightAngle < 0) {
            lightAngle = 0;
        } else if (lightAngle > 180) {
            lightAngle = 180;
        }
        System.out.println(lightAngle);
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
//        lightDirection.normalize();
        System.out.println(lightDirection.x + " " + lightDirection.y + " " + lightDirection.z);

        // Update camera position
//        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
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

    private void checkCameraInc() {
        boolean b;
        switch ((int) cameraInc.x) {
            case -1:
                b = playerItem.canMove("left");
                if(!b) cameraInc.x = 0f;
                break;
            case 1:
                b = playerItem.canMove("right");
                if(!b) cameraInc.x = 0f;
                break;
        }

        switch ((int) cameraInc.y) {
            case -1:
                b = playerItem.canMove("down");
                if(!b) cameraInc.y = 0f;
                break;
            case 1:
                b = playerItem.canMove("up");
                if(!b) cameraInc.y = 0f;
                break;
        }
    }

    @Override
    public void cleanup() {
        renderer.cleanup();

        scene.cleanup();
    }

    public void animateCameraRotation(float stepChange, float maxValue) {
        float xRotation = camera.getRotation().x;
        float yRotation = camera.getRotation().y;
        if (cameraInc.x == 0) {
            yRotation -= Math.signum(yRotation) * stepChange;
        } else {
            yRotation += cameraInc.x * stepChange;
            if(Math.abs(yRotation) > maxValue) {
                yRotation = Math.signum(yRotation) * maxValue;
            }
        }
        if(cameraInc.y == 0) {
            xRotation -= Math.signum(xRotation) * stepChange;
        } else {
            xRotation -= cameraInc.y * stepChange;
            if(Math.abs(xRotation) > maxValue) {
                xRotation = Math.signum(xRotation) * maxValue;
            }
        }
        camera.setRotation(xRotation, yRotation, camera.getRotation().z);
    }

}
