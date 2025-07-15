#version 150

in vec2 v_uv;
out vec4 fragColor;

uniform vec3 u_cameraPos;
uniform vec3 u_sphereCenter;
uniform float u_sphereRadius;

uniform mat4 u_inverseProjectionMatrix;
uniform mat4 u_inverseViewMatrix;

void main() {
    vec2 screenPos = v_uv * 2.0 - 1.0;

    vec4 rayClip = vec4(screenPos, -1.0, 1.0);
    vec4 rayEye = u_inverseProjectionMatrix * rayClip;
    rayEye = vec4(rayEye.xy, -1.0, 0.0);

    vec3 rayWorld = normalize((u_inverseViewMatrix * rayEye).xyz);
    vec3 origin = u_cameraPos;
    vec3 oc = origin - u_sphereCenter;

    float b = dot(oc, rayWorld);
    float c = dot(oc, oc) - u_sphereRadius * u_sphereRadius;
    float h = b * b - c;

    if (h < 0.0) {
        discard;
    }

    float sqrtH = sqrt(h);
    float t = -b - sqrtH;

    if (t < 0.0) {
        t = -b + sqrtH;
    }

    if (t < 0.0) {
        discard;
    }

    vec3 hit = origin + t * rayWorld;
    vec3 normal = normalize(hit - u_sphereCenter);
    float lighting = dot(normal, normalize(vec3(0.2, 1.0, 0.3)));

    fragColor = vec4(vec3(1.0, 0.3, 0.3), 0.5);
}