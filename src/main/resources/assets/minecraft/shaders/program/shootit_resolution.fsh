uniform sampler2D DiffuseSampler;

uniform vec2 OutSize;
uniform float Radius;

varying vec2 texCoord;
varying vec2 oneTexel;

void main() {
	if((int)((1.0 - texCoord.x) * OutSize.x) % (int)Radius == 0 && (int)((1.0 - texCoord.y) * OutSize.y) % (int)Radius == 0) {
		vec4 color = vec4(0, 0, 0, 0);
		for(float i = 0 ; i < Radius; i += 1.0)
			for(float j = 0; j < Radius; j += 1.0)
				color += texture2D(DiffuseSampler, texCoord + vec2(i, j) * oneTexel);
		gl_FragColor = vec4(color.rgb / Radius / Radius, 1.0);
	}
	else {
		gl_FragColor = texture2D(DiffuseSampler, texCoord);
	}
}