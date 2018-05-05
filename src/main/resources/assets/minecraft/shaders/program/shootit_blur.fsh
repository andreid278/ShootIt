#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float Radius;

void main() {
	vec4 sum = vec4(0.0);
	
	for (float i = -Radius; i <= Radius; i++)
		for(float j = -Radius; j <= Radius; j++)
			sum += texture2D(DiffuseSampler, texCoord + oneTexel * vec2(i, j));
	
	gl_FragColor = vec4(sum.rgb / ((2 * Radius + 1) * (2 * Radius + 1)), 1.0);
}
