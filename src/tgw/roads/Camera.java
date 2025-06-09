package tgw.roads;

import org.joml.Matrix4f;

public class Camera {

    private static final float ACCELERATION = 8.0f / 9;
    private static final float DAMPING = 0.9f;
    private final Matrix4f projectionMatrix;
    private float roll = (float) (Math.PI / 2.0);
    private final Matrix4f viewMatrix;
    private float vx;
    private float vy;
    private final Window window;
    private float x;
    private float y;
    private float zoom = 1.0f;

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
        float width = this.window.getWidth() * 0.5f * this.zoom;
        float height = this.window.getHeight() * 0.5f * this.zoom;
        this.projectionMatrix.ortho(-width, width, -height, height, 0, 100);
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        this.viewMatrix.identity();
        this.viewMatrix.lookAt(this.x, this.y, 100, this.x, this.y, -1, (float) Math.cos(this.roll), (float) Math.sin(this.roll), 0);
        return this.viewMatrix;
    }

    public void tick() {
        if (KeyListener.ROTATE_LEFT.isDown()) {
            this.roll += (float) (Math.PI / 180.0);
        }
        if (KeyListener.ROTATE_RIGHT.isDown()) {
            this.roll -= (float) (Math.PI / 180.0);
        }
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
        float lastZoom = this.zoom;
        float scrollY = MouseListener.getScrollY();
        if (scrollY > 0) {
            this.zoom /= 2;
            if (this.zoom < 1 / 16.0f) {
                this.zoom = 1 / 16.0f;
            }
        }
        else if (scrollY < 0) {
            this.zoom *= 2;
            if (this.zoom > 16) {
                this.zoom = 16;
            }
        }
        if (lastZoom != this.zoom) {
            this.adjustProjection();
        }
    }
}
