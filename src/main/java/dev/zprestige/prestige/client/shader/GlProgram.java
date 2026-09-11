package dev.zprestige.prestige.client.shader;

/**
 * 1.21.11 port note:
 * The old core-shader API (ShaderProgram/GlUniform/RenderSystem.setShader) was
 * removed in the 1.21.5+ render pipeline rewrite. Custom GL shader programs are
 * no longer practical for a client mod, so the gradient glow is now drawn with
 * plain geometry in GradientGlowShader/RenderUtil. This base class only remains
 * to keep the module structure intact.
 */
public abstract class GlProgram {
    protected void setup() {
    }

    public void use() {
    }
}
