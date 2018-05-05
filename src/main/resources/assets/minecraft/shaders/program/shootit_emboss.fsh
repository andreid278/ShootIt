uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform float Contrast;
uniform float Colored;

void main() {
	vec3 color = vec3(0.5);
	
	color += texture2D(DiffuseSampler, texCoord - oneTexel).rgb * Contrast;
	color -= texture2D(DiffuseSampler, texCoord + oneTexel).rgb * Contrast;
	
	if(Colored == 0.0) {
		color = vec3(0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b);
	}
	
	gl_FragColor = vec4(color, 1);
}