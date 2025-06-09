package tgw.roads;

import org.joml.Matrix4f;

public class Camera {

    private static final float ACCELERATION = 8.0f / 9;
    private static final float DAMPING = 0.9f;
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private float vx;
    private float vy;
    private final Window window;
    private float x;
    private float y;

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
        float width = this.window.getWidth() * 0.5f;
        float height = this.window.getHeight() * 0.5f;
        this.projectionMatrix.ortho(-width, width, -height, height, 0, 100);
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        this.viewMatrix.identity();
        this.viewMatrix.lookAt(this.x, this.y, 100, this.x, this.y, -1, 0, 1, 0);
        return this.viewMatrix;
    }

    public void tick() {
        if (KeyListener.FORWARD.isDown()) {
            this.vy += ACCELERATION;
        }
        if (KeyListener.BACKWARD.isDown()) {
            this.vy -= ACCELERATION;
        }
        this.vy *= DAMPING;
        this.y += this.vy;
        if (KeyListener.RIGHT.isDown()) {
            this.vx += ACCELERATION;
        }
        if (KeyListener.LEFT.isDown()) {
            this.vx -= ACCELERATION;
        }
        this.vx *= DAMPING;
        this.x += this.vx;
    }
}
