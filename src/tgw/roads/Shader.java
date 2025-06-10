package tgw.roads;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private static int currentProgramId;
    private final int programId;

    public Shader(String name) {
        this(name, name);
    }

    public Shader(String fragmentName, String vertexName) {
        String fragmentSrc;
        String vertexSrc;
        try {
            fragmentSrc = Files.readString(Path.of(".\\res\\shaders\\" + fragmentName + ".fsh"));
            vertexSrc = Files.readString(Path.of(".\\res\\shaders\\" + vertexName + ".vsh"));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        int vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexSrc);
        glCompileShader(vertexId);
        if (glGetShaderi(vertexId, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("Compilation of vertex shader failed!");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            throw new RuntimeException("Compilation of vertex shader failed!");
        }
        int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentSrc);
        glCompileShader(fragmentId);
        if (glGetShaderi(fragmentId, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("Compilation of fragment shader failed!");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            throw new RuntimeException("Compilation of fragment shader failed!");
        }
        this.programId = glCreateProgram();
        glAttachShader(this.programId, vertexId);
        glAttachShader(this.programId, fragmentId);
        glLinkProgram(this.programId);
        if (glGetProgrami(this.programId, GL_LINK_STATUS) == GL_FALSE) {
            int len = glGetShaderi(this.programId, GL_INFO_LOG_LENGTH);
            System.out.println("Shader linking failed!");
            System.out.println(glGetProgramInfoLog(this.programId, len));
            throw new RuntimeException("Shader linking failed!");
        }
    }

    public static void unbind() {
        if (currentProgramId != 0) {
            glUseProgram(0);
            currentProgramId = 0;
        }
    }

    public void bind() {
        if (currentProgramId != this.programId) {
            glUseProgram(this.programId);
            currentProgramId = this.programId;
        }
    }

    public void uploadMat4f(String varName, Matrix4f mat) {
        int varLoc = glGetUniformLocation(this.programId, varName);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        mat.get(buffer);
        glUniformMatrix4fv(varLoc, false, buffer);
    }
}
