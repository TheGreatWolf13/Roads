package tgw.roads;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import tgw.roads.util.Nullable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;

public final class Window {

    public static final float[] CIRCLE_VERTEX_ARRAY;
    public static final int[] CIRCLE_ELEMENT_ARRAY;
    private static final int CIRCLE_QUALITY = 64;
    private static double lastFrameTime;
    private static @Nullable Window window;

    static {
        float[] vertex = new float[(CIRCLE_QUALITY + 1) * 3];
        int[] elements = new int[CIRCLE_QUALITY + 2];
        int index = 3;
        for (float i = 0; i < 360; i += 360.0f / CIRCLE_QUALITY) {
            vertex[index++] = (float) Math.cos(Math.PI / 180 * i);
            vertex[index++] = (float) Math.sin(Math.PI / 180 * i);
            ++index;
        }
        for (int i = 0; i < CIRCLE_QUALITY + 2 - 1; i++) {
            elements[i] = i;
        }
        elements[CIRCLE_QUALITY + 2 - 1] = 1;
        CIRCLE_VERTEX_ARRAY = vertex;
        CIRCLE_ELEMENT_ARRAY = elements;
    }

    private final Camera camera;
    private int height = 480;
    private final Shader shader;
    private final int vaoId;
    private int width = 640;
    private final long windowPointer;

    private Window() {
        System.out.println("Starting OpenGL version " + Version.getVersion());
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        this.windowPointer = GLFW.glfwCreateWindow(640, 480, "", MemoryUtil.NULL, MemoryUtil.NULL);
        if (this.windowPointer == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to create GLFW window!");
        }
        GLFW.glfwSetCursorPosCallback(this.windowPointer, MouseListener::mousePosCallback);
        GLFW.glfwSetMouseButtonCallback(this.windowPointer, MouseListener::mouseButtonCallback);
        GLFW.glfwSetScrollCallback(this.windowPointer, MouseListener::mouseScrollCallback);
        GLFW.glfwSetKeyCallback(this.windowPointer, KeyListener::keyCallback);
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        assert vidmode != null;
        GLFW.glfwSetWindowPos(this.windowPointer, (vidmode.width() - 640) / 2, (vidmode.height() - 480) / 2);
        GLFW.glfwMakeContextCurrent(this.windowPointer);
        GLFW.glfwSwapInterval(0);
        GLFW.glfwShowWindow(this.windowPointer);
        GL.createCapabilities();
        this.shader = new Shader("default");
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(CIRCLE_VERTEX_ARRAY.length);
        vertexBuffer.put(CIRCLE_VERTEX_ARRAY).flip();
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(CIRCLE_ELEMENT_ARRAY.length);
        elementBuffer.put(CIRCLE_ELEMENT_ARRAY).flip();
        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        GLFW.glfwSetFramebufferSizeCallback(this.windowPointer, Window::onSizeChange);
        this.camera = new Camera(this, 0, 0);
    }

    public static Window get() {
        if (window == null) {
            window = new Window();
        }
        return window;
    }

    private static void limitDisplayFPS(int fps) {
        double nextFrameTime = lastFrameTime + 1.0 / (fps + 1);
        double now = GLFW.glfwGetTime();
        while (now < nextFrameTime) {
            //We use 800 instead of 1000 to compensate for the OS overhead
            long ms = (long) ((nextFrameTime - now) * 800);
            try {
                Thread.sleep(ms);
            }
            catch (InterruptedException ignored) {
            }
            now = GLFW.glfwGetTime();
        }
        lastFrameTime = now;
    }

    private static void onSizeChange(long ignoredWindowPointer, int width, int height) {
        Window window = get();
        window.width = width;
        window.height = height;
        window.camera.adjustProjection();
        glViewport(0, 0, width, height);
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public void loop() {
        int frames = 0;
        String fps = "";
        long lastTime = System.nanoTime() / 1_000_000L;
        while (!GLFW.glfwWindowShouldClose(this.windowPointer)) {
            this.camera.tick();
            if (MouseListener.consumeClick(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                Vector4f vec = MouseListener.getVec(this);
                vec.mul(this.camera.getInverseProjMatrix()).mul(this.camera.getInverseViewMatrix());
                Node.createNew(vec.x, vec.y);
            }
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            GLFW.glfwSetWindowTitle(this.windowPointer, fps);
            this.shader.bind();
            this.shader.uploadMat4f("uProj", this.camera.getProjMatrix());
            this.shader.uploadMat4f("uView", this.camera.getViewMatrix());
            this.shader.uploadVec3f("scale", 1, 1, 1);
            this.shader.uploadVec3f("offset", 0, 0, 0);
            this.shader.setShaderColour(1, 1, 1, 1);
            glBindVertexArray(this.vaoId);
            glEnableVertexAttribArray(0);
            glDrawElements(GL_TRIANGLE_FAN, CIRCLE_ELEMENT_ARRAY.length, GL_UNSIGNED_INT, 0);
            this.shader.uploadVec3f("offset", 1, 1, 0);
            this.shader.setShaderColour(1, 0, 0, 1);
            glDrawElements(GL_TRIANGLE_FAN, CIRCLE_ELEMENT_ARRAY.length, GL_UNSIGNED_INT, 0);
            for (Node value : Node.NODES.values()) {
                this.shader.uploadVec3f("offset", (float) value.getX(), (float) value.getY(), 0);
                this.shader.setShaderColour(0, 1, 0, 1);
                glDrawElements(GL_TRIANGLE_FAN, CIRCLE_ELEMENT_ARRAY.length, GL_UNSIGNED_INT, 0);
            }
            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
            Shader.unbind();
            this.updateDisplay();
            limitDisplayFPS(120);
            ++frames;
            long time = System.nanoTime() / 1_000_000L;
            while (time >= lastTime + 1_000L) {
                lastTime += 1_000;
                fps = frames + " FPS";
                frames = 0;
            }
        }
        GLFW.glfwTerminate();
    }

    private void updateDisplay() {
        GLFW.glfwPollEvents();
        GLFW.glfwSwapBuffers(this.windowPointer);
        GLFW.glfwPollEvents();
        MouseListener.endFrame();
    }
}
