#version 150

in vec2 v_uv;
out vec4 fragColor;

uniform vec3 u_cameraPos;
uniform vec3 u_sphereCenter;
uniform float u_sphereRadius;

uniform mat4 u_inverseProjectionMatrix;
uniform mat4 u_inverseViewMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

void main() {
    // Convert UV to NDC
    vec2 ndc = v_uv * 2.0 - 1.0;

    // Reconstruct world position
    vec4 clipPos = vec4(ndc, 1.0, 1.0);
    vec4 viewPos = u_inverseProjectionMatrix * clipPos;
    viewPos /= viewPos.w;

    vec4 worldPos = u_inverseViewMatrix * viewPos;

    // Calculate ray direction
    vec3 rayDir = normalize(worldPos.xyz - u_cameraPos);

    // Ray-sphere intersection
    vec3 oc = u_cameraPos - u_sphereCenter;
    float a = dot(rayDir, rayDir);
    float b = 2.0 * dot(oc, rayDir);
    float c = dot(oc, oc) - u_sphereRadius * u_sphereRadius;

    float discriminant = b * b - 4.0 * a * c;
    if (discriminant < 0.0) {
        discard;
    }

    float t = (-b - sqrt(discriminant)) / (2.0 * a);
    if (t < 0.0) {
        t = (-b + sqrt(discriminant)) / (2.0 * a);
    }
    if (t < 0.0) {
        discard;
    }

    // Compute hit point
    vec3 hitPoint = u_cameraPos + t * rayDir;
    vec3 normal = normalize(hitPoint - u_sphereCenter);

    // ==== ГЛУБИНА ====

    // Мировые -> клип координаты
    vec4 clipSpacePos = u_projectionMatrix * u_viewMatrix * vec4(hitPoint, 1.0);

    // Переводим Z из clip-space в normalized device coordinates
    float ndcZ = clipSpacePos.z / clipSpacePos.w;

    // Переводим из NDC [-1, 1] → depth [0, 1]
    float depth = ndcZ * 0.5 + 0.5;

    gl_FragDepth = depth;

    // Цвет сферы
    fragColor = vec4(vec3(1.0, 0.3, 0.3), 0.8);
}