#version 150

in vec2 v_uv;
out vec4 fragColor;

uniform vec3 u_cameraPos;
uniform vec3 u_sphereCenter;
uniform float u_sphereRadius;

uniform mat4 u_inverseProjectionMatrix;
uniform mat4 u_inverseViewMatrix;
uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;

void main() {
    // Convert UV to NDC
    vec2 ndc = v_uv * 2.0 - 1.0;

    // Reconstruct view ray from NDC
    vec4 clipPos = vec4(ndc, -1.0, 1.0); // z = -1 (near plane)
    vec4 viewPos = u_inverseProjectionMatrix * clipPos;
    viewPos /= viewPos.w;

    vec4 worldPos = u_inverseViewMatrix * viewPos;
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

    // Compute world-space hit point
    vec3 hitPoint = u_cameraPos + t * rayDir;
    vec4 hitWorld = vec4(hitPoint, 1.0);

    // Compute clip space position to get depth
    vec4 viewSpace = u_viewMatrix * hitWorld;
    vec4 clipSpace = u_projectionMatrix * viewSpace;

    // Perspective divide to get NDC
    vec3 ndcSpace = clipSpace.xyz / clipSpace.w;

    // Convert NDC Z to depth
    float depth = (ndcSpace.z * 0.5) + 0.5;

    // Write depth
    gl_FragDepth = depth;

    // Lighting
    vec3 normal = normalize(hitPoint - u_sphereCenter);

    float skyLight = max(0.0, dot(normal, vec3(0.0, 1.0, 0.0)));
    vec3 lightDir = normalize(vec3(0.5, 0.8, 0.3));
    float directionalLight = max(0.0, dot(normal, lightDir));
    float ambientLight = 0.2;

    float totalLight = ambientLight + skyLight * 0.4 + directionalLight * 0.6;
    totalLight = clamp(totalLight, 0.0, 1.0);

    vec3 baseColor = vec3(0.5, 0.0, 1.0);
    vec3 finalColor = baseColor * totalLight;

    fragColor = vec4(finalColor, 1.0);
}