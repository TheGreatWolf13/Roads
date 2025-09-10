#version 330 core

layout (location=0) in vec3 vertexPos;

uniform mat4 uProj;
uniform mat4 uView;
uniform vec3 offset;
uniform vec3 scale;
uniform vec4 colour;

out vec4 fColor;

void main() {
    fColor = colour;
    gl_Position = uProj * uView * vec4(vertexPos * scale + offset, 1.0);
}