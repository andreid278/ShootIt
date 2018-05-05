uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform float Coeff;

void main() {
	vec4 color = texture2D(DiffuseSampler, texCoord);
	
	color.rgb = pow(color.rgb, vec3(1.0 / Coeff));
	
	gl_FragColor = vec4(color.rgb, 1.0);
}