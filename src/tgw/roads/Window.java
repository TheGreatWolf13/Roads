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
    private final int[] elementArray = {
            //CCW order
            2, 1, 0, //Top right
            0, 1, 3, //Bottom left

    };
    private final int shaderId;
    private final int vaoId;
    private final float[] vertexArray = {
            //pos: 3 float
            //colour: 4 float
            0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, //Bottom right
            -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, //Top left
            0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, //Top right
            -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f //Bottom left
    };
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
        int vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, VERTEX_SOURCE);
        glCompileShader(vertexId);
        if (glGetShaderi(vertexId, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("Compilation of vertex shader failed!");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            throw new RuntimeException("Compilation of vertex shader failed!");
        }
        int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, FRAGMENT_SOURCE);
        glCompileShader(fragmentId);
        if (glGetShaderi(fragmentId, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("Compilation of fragment shader failed!");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            throw new RuntimeException("Compilation of fragment shader failed!");
        }
        this.shaderId = glCreateProgram();
        glAttachShader(this.shaderId, vertexId);
        glAttachShader(this.shaderId, fragmentId);
        glLinkProgram(this.shaderId);
        if (glGetProgrami(this.shaderId, GL_LINK_STATUS) == GL_FALSE) {
            int len = glGetShaderi(this.shaderId, GL_INFO_LOG_LENGTH);
            System.out.println("Shader linking failed!");
            System.out.println(glGetProgramInfoLog(this.shaderId, len));
            throw new RuntimeException("Shader linking failed!");
        }
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(this.vertexArray.length);
        vertexBuffer.put(this.vertexArray).flip();
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
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            ++frames;
            double time = GLFW.glfwGetTime();
            if (time - lastTime >= 1) {
                lastTime = time;
                fps = frames + " FPS";
                frames = 0;
            }
            GLFW.glfwSetWindowTitle(this.windowPointer, fps);
            glUseProgram(this.shaderId);
            glBindVertexArray(this.vaoId);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glDrawElements(GL_TRIANGLES, this.elementArray.length, GL_UNSIGNED_INT, 0);
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);
            glUseProgram(0);
            GLFW.glfwSetFramebufferSizeCallback(this.windowPointer, (w, width, height) -> glViewport(0, 0, width, height));
            GLFW.glfwSwapBuffers(this.windowPointer);
        }
        GLFW.glfwTerminate();
    }
}
