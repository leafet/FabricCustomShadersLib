#version 150

in vec3 Position;
out vec2 v_uv;

void main() {
    // UV из позиции [-1,1] → [0,1]
    v_uv = Position.xy * 0.5 + 0.5;
    gl_Position = vec4(Position, 1.0);
}