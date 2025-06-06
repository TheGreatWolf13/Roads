package tgw.roads;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private final Vector2f position;
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
    }

    public void adjustProjection() {
        this.projectionMatrix.identity();
        this.projectionMatrix.ortho(0, 32 * 40, 0, 21 * 32, 0, 100);
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0, 0, -1);
        Vector3f cameraUp = new Vector3f(0, 1, 0);
        this.viewMatrix.identity();
        this.viewMatrix.lookAt(new Vector3f(this.position.x, this.position.y, 20), cameraFront.add(this.position.x, this.position.y, 0), cameraUp);
        return this.viewMatrix;
    }
}
