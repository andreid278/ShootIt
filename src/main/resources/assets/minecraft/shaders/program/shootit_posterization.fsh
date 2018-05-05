uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform float Gamma;
uniform float NumColors;

void main() {
	vec3 color = texture2D(DiffuseSampler, texCoord).rgb;
	
	color = pow(color, vec3(Gamma, Gamma, Gamma));
	color = color * NumColors;
	color = floor(color);
	color = color / NumColors;
	color = pow(color, vec3(1.0 / Gamma));
	
	gl_FragColor = vec4(color, 1.0);
}