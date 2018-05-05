uniform sampler2D DiffuseSampler;

uniform sampler2D DepthSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform float BlurRadius;
uniform float BlurDepth;

void main() {
	float n = 0.5;
	float f = 500.0;
	
	float depth = 2.0 * n / (f + n - texture(DepthSampler, texCoord).r * (f - n));
	
	float rad = floor(BlurRadius * abs(BlurDepth - depth));
	
	vec4 sum = vec4(0.0);
	
	for (float i = -rad; i <= rad; i++)
		for(float j = -rad; j <= rad; j++)
			sum += texture2D(DiffuseSampler, texCoord + oneTexel * vec2(i, j));
	
	gl_FragColor = vec4(sum.rgb / ((2 * rad + 1) * (2 * rad + 1)), 1.0);
}