package tgw.roads;

import org.joml.Matrix4f;

public class Camera {

    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Window window;
    public float x;
    public float y;

    public Camera(Window window, float x, float y) {
        this.window = window;
        this.x = x;
        this.y = y;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.adjustProjection();
    }

    public void adjustProjection() {
        this.projectionMatrix.identity();
        this.projectionMatrix.ortho(0, this.window.getWidth(), 0, this.window.getHeight(), 0, 100);
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        this.viewMatrix.identity();
        this.viewMatrix.lookAt(this.x, this.y, 20, this.x, this.y, -1, 0, 1, 0);
        return this.viewMatrix;
    }
}
