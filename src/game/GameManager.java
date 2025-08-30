package game;

import engine.MouseInput;
import engine.Scene;
import engine.graph.Camera;
import engine.items.GameItem;
import engine.items.SkyBox;
import game.world.MapManger;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static engine.MouseInput.MOUSE_SENSITIVITY;

public class GameManager {
    Scene scene;
    Camera camera;
    LightController lightController;
    MapManger mapManger;
    MainPlayer player;
    public GameManager(Scene scene, Camera camera) {
        this.camera = camera;
        this.scene = scene;
        lightController = new LightController(scene);
    }
    public void init() throws Exception {
        camera.getPosition().x = 0f;
        camera.getPosition().y = 0f;
        camera.getPosition().z = 4f;
        camera.getRotation().x = 0.0f;
        camera.getRotation().y = 0.0f;
        camera.getRotation().z = 0f;

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
        lightController.setupLights();

        mapManger = new MapManger(1000, 100);
        mapManger.generateMap(scene);


        player = new MainPlayer(mapManger);
        player.setSpeed(0.05f); // 0.01f


        mapManger.putPlayerOnMapCenter(camera, player);

        scene.setGameItems(new GameItem[] { player});
    }

    public void updateGameState(Vector3f cameraInc, float angleInc, MouseInput mouseInput) {
        if (mouseInput.isRightButtonPressed()) {
            // Update camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
//        System.out.println("--------");
//        System.out.println(camera.getPosition().x + " " + camera.getPosition().y + " " + camera.getPosition().z);


        checkCameraInc(cameraInc);

        Vector2f realMovement = player.move(new Vector2f(cameraInc.x, cameraInc.y));

        cameraInc.set(realMovement, cameraInc.z);

//        camera.animateCameraRotation(cameraInc, 0.25f, 2.5f);

        camera.setPosition(player.getPosition().x, player.getPosition().y, camera.getPosition().z + cameraInc.z * 0.05f);

//        if(sceneChanged) {
//            checkCameraInc();
//
//            playerItem.move(new Vector2f(cameraInc.x, cameraInc.y));
//
//            camera.setPosition(playerItem.getPosition().x, playerItem.getPosition().y, camera.getPosition().z);
//
//        }

//        lightAngle += angleInc;
//        if (lightAngle < 0) {
//            lightAngle = 0;
//        } else if (lightAngle > 180) {
//            lightAngle = 180;
//        }
//        System.out.println(lightAngle);
//        float xValue = (float) Math.cos(Math.toRadians(lightAngle));
//        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
//        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
//        lightDirection.x = xValue;
//        lightDirection.y = yValue;
//        lightDirection.normalize();
//        System.out.println(lightDirection.x + " " + lightDirection.y + " " + lightDirection.z);

        // Update camera position
//        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
    }

    private void checkCameraInc(Vector3f cameraInc) {
        boolean b;
        switch ((int) cameraInc.x) {
            case -1:
                b = player.canMove("left");
                if(!b) cameraInc.x = 0f;
                break;
            case 1:
                b = player.canMove("right");
                if(!b) cameraInc.x = 0f;
                break;
        }

        switch ((int) cameraInc.y) {
            case -1:
                b = player.canMove("down");
                if(!b) cameraInc.y = 0f;
                break;
            case 1:
                b = player.canMove("up");
                if(!b) cameraInc.y = 0f;
                break;
        }
    }
}
