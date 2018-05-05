uniform sampler2D DiffuseSampler;

uniform float Red;
uniform float Green;
uniform float Blue;

varying vec2 texCoord;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);
    color.x *= Red;
    color.y *= Green;
    color.z *= Blue;
    color.a = 1.0;
	gl_FragColor = color;
}