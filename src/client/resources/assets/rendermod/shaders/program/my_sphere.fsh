#version 150

in vec4 vertexColor;

out vec4 fragColor;

void main() {

    vec4 color = vertexColor;
    if (color.a == 0.0) {
        discard;
    }

    fragColor = vertexColor;
}