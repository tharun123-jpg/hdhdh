package dev.zprestige.prestige.client.shader.impl;

import dev.zprestige.prestige.client.Prestige;
import dev.zprestige.prestige.client.event.EventListener;
import dev.zprestige.prestige.client.event.impl.ResolutionChangeEvent;
import dev.zprestige.prestige.client.shader.GlProgram;
import dev.zprestige.prestige.client.util.impl.RenderUtil;

import java.awt.Color;

/**
 * 1.21.11 port of the old gradient-glow core shader.
 * The fragment shader computed a 4-corner bilinear gradient inside a rounded
 * rectangle (SDF) with a soft outer glow. That is reproduced with plain
 * POSITION_COLOR geometry, so no custom shader program is needed anymore.
 */
public class GradientGlowShader extends GlProgram {

    public float x;
    public float y;
    public float width;
    public float height;
    public float radius;
    public float softness;
    public Color color1 = new Color(0, 0, 0, 0);
    public Color color2 = new Color(0, 0, 0, 0);
    public Color color3 = new Color(0, 0, 0, 0);
    public Color color4 = new Color(0, 0, 0, 0);

    public GradientGlowShader() {
        Prestige.Companion.getEventBus().registerListener(this);
    }

    public void setParameters(float f, float f2, float f3, float f4, float f5, float f6, Color color, Color color2, Color color3, Color color4) {
        // old shader math: rect at (f, f2) with size (f3, f4) in scaled GUI coords, radius = f5*2, softness = f6
        this.x = f;
        this.y = f2;
        this.width = f3;
        this.height = f4;
        this.radius = f5 * 2.0f;
        this.softness = f6;
        this.color1 = color;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
    }

    @Override
    public void use() {
        RenderUtil.drawGradientGlow(x, y, width, height, radius, softness, color1, color2, color3, color4);
    }

    @EventListener
    public void event(ResolutionChangeEvent event) {
        // no framebuffer anymore, nothing to resize
    }
}
