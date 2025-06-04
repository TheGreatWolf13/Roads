package tgw.roads;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;
import tgw.roads.util.Nullable;

public final class Window {

    private static final String VERTEX_SOURCE = """
            #version 330 core
            
            layout (location=0) in vec3 aPos;
            layout (location=1) in vec4 aColor;
            
            out vec4 fColor;
            
            void main() {
                fColor = aColor;
                gl_Position = vec4(aPos, 1.0);
            }
            """;
    private static final String FRAGMENT_SOURCE = """
            #version 330 core
            
            in vec4 fColor;
            
            out vec4 color;
            
            void main() {
                color = fColor;
            }
            """;
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
        int vertexId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexId, VERTEX_SOURCE);
        GL20.glCompileShader(vertexId);
        if (GL20.glGetShaderi(vertexId, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(vertexId, GL20.GL_INFO_LOG_LENGTH);
            System.out.println("Compilation of vertex shader failed!");
            System.out.println(GL20.glGetShaderInfoLog(vertexId, len));
            throw new RuntimeException("Compilation of vertex shader failed!");
        }
        int fragmentId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentId, FRAGMENT_SOURCE);
        GL20.glCompileShader(fragmentId);
        if (GL20.glGetShaderi(fragmentId, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(fragmentId, GL20.GL_INFO_LOG_LENGTH);
            System.out.println("Compilation of fragment shader failed!");
            System.out.println(GL20.glGetShaderInfoLog(fragmentId, len));
            throw new RuntimeException("Compilation of fragment shader failed!");
        }
        int shaderId = GL20.glCreateProgram();
        GL20.glAttachShader(shaderId, vertexId);
        GL20.glAttachShader(shaderId, fragmentId);
        GL20.glLinkProgram(shaderId);
        if (GL20.glGetProgrami(shaderId, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(shaderId, GL20.GL_INFO_LOG_LENGTH);
            System.out.println("Shader linking failed!");
            System.out.println(GL20.glGetProgramInfoLog(shaderId, len));
            throw new RuntimeException("Shader linking failed!");
        }
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
