package engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    
    private final Vector3f rotation;
    
    private Matrix4f viewMatrix;
    
    public Camera() {
        position = new Vector3f();
        rotation = new Vector3f();
        viewMatrix = new Matrix4f();
    }
    
    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
    
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
    
    public Matrix4f updateViewMatrix() {
        return Transformation.updateGenericViewMatrix(position, rotation, viewMatrix);
    }
    
    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public Vector3f getRotation() {
        return rotation;
    }
    
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

    public void animateCameraRotation(Vector3f cameraInc, float stepChange, float maxValue) {
        float xRotation = getRotation().x;
        float yRotation = getRotation().y;
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
        setRotation(xRotation, yRotation, getRotation().z);
    }
}