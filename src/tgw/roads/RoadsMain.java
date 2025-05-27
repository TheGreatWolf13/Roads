package tgw.roads;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public final class RoadsMain {

    private RoadsMain() {}

    private static long init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        long window = GLFW.glfwCreateWindow(640, 480, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to create GLFW window!");
        }
        GLFW.glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(w, true);
            }
        });
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        assert vidmode != null;
        GLFW.glfwSetWindowPos(window, (vidmode.width() - 640) / 2, (vidmode.height() - 480) / 2);
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);
        return window;
    }

    private static void loop(long window) {
        GL.createCapabilities();
        int frames = 0;
        String fps = "";
        double lastTime = GLFW.glfwGetTime();
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            ++frames;
            double time = GLFW.glfwGetTime();
            if (time - lastTime >= 1) {
                lastTime = time;
                fps = frames + " FPS";
                frames = 0;
            }
            GLFW.glfwSetWindowTitle(window, fps);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(-0.5f, -0.5f);
            GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            GL11.glVertex2f(-0.5f, 0.5f);
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            GL11.glVertex2f(1, 1);
            GL11.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
            GL11.glVertex2f(1, -1);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnd();
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        long window = init();
        loop(window);
    }
}