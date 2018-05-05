#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;
uniform float BokehCoeff;

void main() {
	vec4 sum = vec4(0.0);
	vec4 msum = vec4(0.0);

	vec2 startDir = -0.5 * BlurDir * (Radius - 1.0) * oneTexel;
	for (int i = 0; i < Radius; i++) {
		vec4 color = texture2D(DiffuseSampler, texCoord + startDir + oneTexel * BlurDir * float(i));
		sum += color;
		msum = max(color, msum);
	}
	
	sum /= Radius;
	
	gl_FragColor = mix(sum, msum, BokehCoeff);
}
