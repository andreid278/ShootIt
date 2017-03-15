uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

void main() {
    vec4 magdir = texture2D(DiffuseSampler, texCoord);
	float alpha = 0.5 / sin(3.14159 / 8);
	float2 offset = round(alpha.xx * magdir.xy / magdir.zz);
	float4 fwdneighbour = texture2D(DiffuseSampler, texCoord + offset);
	float4 backneighbour = texture2D(DiffuseSampler, texCoord - offset);
	float4 colorO;
	
	if(fwdneighbour.z > magdir.z || backneighbour.z > magdir.z)
		colorO = float4(0.0, 0.0, 0.0, 0.0);
	else colorO = float4(1.0, 1.0, 1.0, 1.0);
	
	gl_FragColor = colorO;
}