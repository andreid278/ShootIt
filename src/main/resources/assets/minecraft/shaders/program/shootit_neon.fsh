uniform sampler2D DiffuseSampler;

uniform float Radius;
uniform float Brightness;

varying vec2 texCoord;
varying vec2 oneTexel;

void main() {
	int i = 0;
	int j = 0;
	vec3 sum = vec3(0.0);
	for(i = -Radius; i <= Radius; i++)
		for(j = -Radius; j <= Radius; j++)
			sum += texture2D(DiffuseSampler, texCoord + oneTexel * vec2(i, j)).rgb * Brightness;
	gl_FragColor = vec4(sum * sum + texture2D(DiffuseSampler, texCoord).rgb, 1.0);
}