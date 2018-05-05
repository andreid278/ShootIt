uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform float Radius;

void main() {
	vec3 m[4];
	vec3 s[4];
	for(int k = 0; k < 4; k++) {
		m[k] = vec3(0.0);
		s[k] = vec3(0.0);
	}
	
	vec4 part[4] = vec4[4] (vec4 (-Radius, -Radius, 0, 0), vec4 (0, -Radius, Radius, 0), vec4 (0, 0, Radius, Radius), vec4 (-Radius, 0, 0, Radius));
	
	for(int k = 0; k < 4; k++) {
		for(int j = part[k].y; j <= part[k].w; j++) {
			for(int i = part[k].x; i <= part[k].z; i++) {
				vec3 c = texture2D (DiffuseSampler, texCoord + vec2(i, j) * oneTexel).rgb;
				m[k] += c;
				s[k] += c * c;
			}
		}
	}
	
	float min_sigma2 = 1e+2;
	float n = float ((Radius + 1) * (Radius + 1));
	for(int k = 0; k < 4; k++) {
		m[k] /= n;
		s[k] = abs (s[k] / n - m[k] * m[k]);
		float sigma2 = s[k].r + s[k].g + s[k].b;
		if(sigma2 < min_sigma2) {
			min_sigma2 = sigma2 ;
			gl_FragColor = vec4 (m[k], 1.0);
		}
	}
}