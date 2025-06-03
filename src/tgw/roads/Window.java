package tgw.roads;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import tgw.roads.util.Nullable;

public final class Window {

    private static @Nullable Window window;
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
    }

    public static Window get() {
        if (window == null) {
            window = new Window();
        }
        return window;
    }

    public void loop() {
        int frames = 0;
        String fps = "";
        double lastTime = GLFW.glfwGetTime();
        float x = 0;
        while (!GLFW.glfwWindowShouldClose(this.windowPointer)) {
            GLFW.glfwPollEvents();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            ++frames;
            double time = GLFW.glfwGetTime();
            if (time - lastTime >= 1) {
                lastTime = time;
                fps = frames + " FPS";
                frames = 0;
            }
            GLFW.glfwSetWindowTitle(this.windowPointer, fps);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            GL11.glVertex2f(-0.5f + x, -0.5f);
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            GL11.glVertex2f(-0.5f + x, 0.5f);
            GL11.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
            GL11.glVertex2f(1 + x, 1);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glVertex2f(1 + x, -1);
            GL11.glEnd();
            GLFW.glfwSetFramebufferSizeCallback(this.windowPointer, (w, width, height) -> GL11.glViewport(0, 0, width, height));
            GLFW.glfwSwapBuffers(this.windowPointer);
        }
        GLFW.glfwTerminate();
    }
}
