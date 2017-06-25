#version 120

attribute vec4 Position;

uniform mat4 ProjMat;

varying vec2 texCoord;

void main() {
	gl_Position = vec4((ProjMat * vec4(Position.xy, 0.0, 1.0)).xy, 0.2, 1.0);
	texCoord = Position.xy;
}
