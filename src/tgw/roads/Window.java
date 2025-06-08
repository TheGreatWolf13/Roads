package tgw.roads;

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

    private static @Nullable Window window;
    private final Camera camera;
    private final int[] elementArray = {
            //CCW order
            2, 1, 0, //Top right
            0, 1, 3, //Bottom left

    };
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
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
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
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(this.windowPointer);
        GL.createCapabilities();
        this.shader = new Shader("default");
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);
        //pos: 3 float
        //colour: 4 float
        //Bottom right
        //Top left
        //Top right
        //Bottom left
        float[] vertexArray = {
                //pos: 3 float
                //colour: 4 float
                100, 0, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, //Bottom right
                0, 100, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, //Top left
                100, 100, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, //Top right
                0, 0, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f //Bottom left
        };
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(this.elementArray.length);
        elementBuffer.put(this.elementArray).flip();
        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 7 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 7 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        this.camera = new Camera(this, 0, 0);
    }

    public static Window get() {
        if (window == null) {
            window = new Window();
        }
        return window;
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
        double lastTime = GLFW.glfwGetTime();
        while (!GLFW.glfwWindowShouldClose(this.windowPointer)) {
            GLFW.glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            ++frames;
            this.camera.x -= 0.5f;
            double time = GLFW.glfwGetTime();
            if (time - lastTime >= 1) {
                lastTime = time;
                fps = frames + " FPS";
                frames = 0;
            }
            GLFW.glfwSetWindowTitle(this.windowPointer, fps);
            this.shader.bind();
            this.shader.uploadMat4f("uProj", this.camera.getProjectionMatrix());
            this.shader.uploadMat4f("uView", this.camera.getViewMatrix());
            glBindVertexArray(this.vaoId);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glDrawElements(GL_TRIANGLES, this.elementArray.length, GL_UNSIGNED_INT, 0);
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);
            Shader.unbind();
            GLFW.glfwSetFramebufferSizeCallback(this.windowPointer, Window::onSizeChange);
            GLFW.glfwSwapBuffers(this.windowPointer);
        }
        GLFW.glfwTerminate();
    }
}
