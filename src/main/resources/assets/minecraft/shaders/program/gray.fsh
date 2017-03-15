uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);
	float res = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
	gl_FragColor = vec4(res, res, res, 1.0);
}