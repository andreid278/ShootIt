uniform sampler2D DiffuseSampler;

uniform float Coeff;

varying vec2 texCoord;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);
    gl_FragColor = vec4 (mix (vec3 (dot (color.rgb, vec3 (0.2126, 0.7152, 0.0722))), color.rgb, Coeff), 1.0);
}