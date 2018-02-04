uniform sampler2D DiffuseSampler;

uniform vec2 OutSize;
uniform float Radius;

varying vec2 texCoord;
varying vec2 oneTexel;

void main() {
	float x = floor(texCoord.x * OutSize.x / Radius) * Radius / OutSize.x;
	float y = floor(texCoord.y * OutSize.y / Radius) * Radius / OutSize.y;
	gl_FragColor = texture2D(DiffuseSampler, vec2(x, y));
}