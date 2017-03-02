#version 330

in vec2 outTexCoord;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec3 colour;

void main() {

	fragColor = vec4(colour, 1) * texture(texture_sampler, outTexCoord);
}