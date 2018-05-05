uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);
    color.rgb = 1.0 - color.rgb;
	gl_FragColor = vec4(color.rgb, 1.0);
}