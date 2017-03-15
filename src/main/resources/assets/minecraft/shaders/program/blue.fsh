uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);
	gl_FragColor = vec4(0.0, 0.0, color.b, 1.0);
}