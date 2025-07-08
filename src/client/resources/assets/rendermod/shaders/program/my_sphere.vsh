#version 150

in vec3 Position;

uniform mat4 ModelMat;
uniform mat4 ModelViewMat;
uniform mat4 ProjectionMat;
uniform vec3 u_playerPos;

out vec4 vertexColor;

void main() {
    vec4 worldPos = ModelMat * vec4(Position, 1.0);
    float dist = distance(worldPos.xyz, u_playerPos);

    if (dist >= 5.0) {
        vertexColor = vec4(1.0, 0.0, 0.0, 0.6);
    } else {
        vertexColor = vec4(1.0, 0.0, 0.0, 0);
    }

    gl_Position = ProjectionMat * ModelViewMat * vec4(Position, 1.0);
}