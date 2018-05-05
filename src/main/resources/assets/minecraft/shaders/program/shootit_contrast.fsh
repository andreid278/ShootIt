uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform float Contrast;
uniform float Brightness;

void main() {
	vec4 color = texture2D(DiffuseSampler, texCoord);
	
	color.rgb = color.rgb * Contrast;
	
	color.rgb += Brightness;
	
	gl_FragColor = vec4(color.rgb, 1.0);
}