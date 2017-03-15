uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

void main() {
	vec4 res = vec4(0.0);
	float alpha = 0.0;
	for(float i = -1; i < 2; i += 1.0)
		for(float j = -1; j < 2; j += 1.0) {
			vec2 dir = vec2(i, j);
			vec4 color = texture2D(DiffuseSampler, texCoord + dir);
			res = res + color;
			alpha = alpha + color.a;
		}
	gl_FragColor = vec4(res.rgb / 25.0, alpha);
}