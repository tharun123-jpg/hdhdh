/*
https://doxbin.com/upload/browniexcodez
*/
package dev.zprestige.prestige.client.util.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.zprestige.prestige.client.Prestige;
import dev.zprestige.prestige.client.shader.impl.GradientGlowShader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

//#SKIDDED
public class RenderUtil {

    public static GradientGlowShader shader;
    public static final Identifier WHITE = Identifier.of("prestige", "textures/gui/white.png");
    public static final int FULL_LIGHT = 15728880; // 0xF000F0

    public static float getTickDelta() {
        return MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
    }

    public static void flush() {
        RenderHelper.immediate.draw();
    }

    /* ------------------------------------------------------------------ */
    /* low level helpers                                                   */
    /* ------------------------------------------------------------------ */

    private static net.minecraft.client.render.VertexConsumer guiBuffer() {
        return RenderHelper.immediate.getBuffer(RenderLayers.text(WHITE));
    }

    private static int argb(Color c) {
        return (c.getAlpha() & 0xFF) << 24 | (c.getRed() & 0xFF) << 16 | (c.getGreen() & 0xFF) << 8 | (c.getBlue() & 0xFF);
    }

    private static void v(Matrix3x2fStack m, net.minecraft.client.render.VertexConsumer vc, float x, float y, int color) {
        vc.vertex(m, x, y).color(color).texture(0.5f, 0.5f).light(FULL_LIGHT);
    }

    private static void v(Matrix4f mat, net.minecraft.client.render.VertexConsumer vc, float x, float y, float z, int color) {
        vc.vertex(mat, x, y, z).color(color).texture(0.5f, 0.5f).light(FULL_LIGHT);
    }

    /* ------------------------------------------------------------------ */
    /* items                                                               */
    /* ------------------------------------------------------------------ */

    public static void renderItem(ItemStack itemStack, float f, float f2, float f3, boolean bl) {
        Matrix3x2fStack matrices = RenderHelper.getGuiMatrices();
        matrices.pushMatrix();
        matrices.scale(f3, f3);
        RenderHelper.getContext().drawItem(itemStack, (int) (f / f3), (int) (f2 / f3));
        if (bl) {
            RenderHelper.getContext().drawItemInSlot(MinecraftClient.getInstance().textRenderer, itemStack, (int) (f / f3), (int) (f2 / f3));
        }
        matrices.popMatrix();
    }

    /* ------------------------------------------------------------------ */
    /* GUI primitives (2D)                                                 */
    /* ------------------------------------------------------------------ */

    public static void renderCircleOutline(float f, float f2, float f3, Color color) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        int c = argb(color);
        float px = f + f3, py = f2;
        for (int i = 5; i <= 360; i += 5) {
            float nx = f + (float) (Math.sin(i * Math.PI / 180) * f3);
            float ny = f2 + (float) (Math.cos(i * Math.PI / 180) * f3);
            quadLine(m, vc, px, py, nx, ny, 1.0f, c);
            px = nx;
            py = ny;
        }
    }

    public static void renderTexturedRect(float f, float f2, float f3, float f4, Identifier identifier, Color color) {
        net.minecraft.client.render.VertexConsumer vc = RenderHelper.immediate.getBuffer(RenderLayers.text(identifier));
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        int c = argb(color);
        v(m, vc, f, f2, c);
        v(m, vc, f, f2 + f4, c);
        v(m, vc, f + f3, f2 + f4, c);
        v(m, vc, f + f3, f2, c);
    }

    public static void renderTexturedQuad(float f, float f2, float f3, float f4, Identifier identifier) {
        renderTexturedQuad(identifier, f, f2, f3, f4, 1.0f, 1, 1, 0, 0);
    }

    public static void renderTexturedQuad(Identifier identifier, float f, float f2, float f3, float f4, float f5, int n, int n2, int n3, int n4) {
        // (id, x, y, u, v, w, h, texW, texH)
        net.minecraft.client.render.VertexConsumer vc = RenderHelper.immediate.getBuffer(RenderLayers.text(identifier));
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        float u0 = f3 / (float) n3;
        float v0 = f4 / (float) n4;
        float u1 = (f3 + n) / (float) n3;
        float v1 = (f4 + n2) / (float) n4;
        int c = 0xFFFFFFFF;
        vc.vertex(m, f, f2).color(c).texture(u0, v0).light(FULL_LIGHT);
        vc.vertex(m, f, f2 + n2).color(c).texture(u0, v1).light(FULL_LIGHT);
        vc.vertex(m, f + n, f2 + n2).color(c).texture(u1, v1).light(FULL_LIGHT);
        vc.vertex(m, f + n, f2).color(c).texture(u1, v0).light(FULL_LIGHT);
    }

    public static void renderTexturedQuad(float f, float f2, float f3, float f4, float f5, int n, int n2, float f6, float f7, int n3, int n4) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = RenderHelper.immediate.getBuffer(RenderLayers.text(WHITE));
        float u0 = f6 / (float) n3;
        float v0 = f7 / (float) n4;
        float u1 = (f6 + n) / (float) n3;
        float v1 = (f7 + n2) / (float) n4;
        int c = 0xFFFFFFFF;
        vc.vertex(m, f, f4).color(c).texture(u0, v1).light(FULL_LIGHT);
        vc.vertex(m, f2, f4).color(c).texture(u1, v1).light(FULL_LIGHT);
        vc.vertex(m, f2, f3).color(c).texture(u1, v0).light(FULL_LIGHT);
        vc.vertex(m, f, f3).color(c).texture(u0, v0).light(FULL_LIGHT);
    }

    public static void renderColoredQuad(float f, float f2, float f3, float f4, Color color) {
        renderColoredQuad(f, f2, f3, f4, color, color, color, color);
    }

    public static void renderColoredQuad(float f, float f2, float f3, float f4, boolean bl, boolean bl2, boolean bl3, boolean bl4, float f5) {
        Color color = new Color(0, 0, 0, 0);
        Color color2 = new Color(0, 0, 0, f5);
        renderColoredQuad(f, f2, f3, f4, bl ? color2 : color, bl2 ? color2 : color, bl3 ? color2 : color, bl4 ? color2 : color);
    }

    public static void renderColoredQuad(float f, float f2, float f3, float f4, Color topLeft, Color topRight, Color bottomLeft, Color bottomRight) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        v(m, vc, f, f2, argb(topLeft));
        v(m, vc, f, f4, argb(bottomLeft));
        v(m, vc, f3, f4, argb(bottomRight));
        v(m, vc, f3, f2, argb(topRight));
    }

    /* ------------------------------------------------------------------ */
    /* rounded rects / circles / misc GUI                                  */
    /* ------------------------------------------------------------------ */

    public static void renderRoundedRectOutline(float f, float f2, float f3, float f4, Color color, float f5) {
        renderRoundedRect(f, f2, f3, f4, f5, color);
    }

    private static void quadLine(Matrix3x2fStack m, net.minecraft.client.render.VertexConsumer vc, float x1, float y1, float x2, float y2, float width, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len < 1.0E-4f) {
            return;
        }
        float nx = -dy / len * width * 0.5f;
        float ny = dx / len * width * 0.5f;
        v(m, vc, x1 + nx, y1 + ny, color);
        v(m, vc, x2 + nx, y2 + ny, color);
        v(m, vc, x2 - nx, y2 - ny, color);
        v(m, vc, x1 - nx, y1 - ny, color);
    }

    /** emits a rounded-rect contour as connected line quads */
    private static void roundedContour(Matrix3x2fStack m, net.minecraft.client.render.VertexConsumer vc, float f, float f2, float f3, float f4, float f5, float width, int color) {
        float px = 0, py = 0;
        boolean first = true;
        int segs = 12;
        // top-left corner
        for (int i = 0; i <= segs; i++) {
            double a = Math.PI / 2 + Math.PI * i / (2.0 * segs);
            float x = f + f5 + (float) (Math.cos(a) * f5);
            float y = f2 + f5 + (float) (Math.sin(a) * f5);
            if (!first) quadLine(m, vc, px, py, x, y, width, color);
            px = x;
            py = y;
            first = false;
        }
        // top-right corner
        for (int i = 0; i <= segs; i++) {
            double a = 0 + Math.PI * i / (2.0 * segs);
            float x = f3 - f5 + (float) (Math.cos(a) * f5);
            float y = f2 + f5 + (float) (Math.sin(a) * f5);
            quadLine(m, vc, px, py, x, y, width, color);
            px = x;
            py = y;
        }
        // bottom-right corner
        for (int i = 0; i <= segs; i++) {
            double a = -Math.PI / 2 + Math.PI * i / (2.0 * segs);
            float x = f3 - f5 + (float) (Math.cos(a) * f5);
            float y = f4 - f5 + (float) (Math.sin(a) * f5);
            quadLine(m, vc, px, py, x, y, width, color);
            px = x;
            py = y;
        }
        // bottom-left corner
        for (int i = 0; i <= segs; i++) {
            double a = Math.PI + Math.PI * i / (2.0 * segs);
            float x = f + f5 + (float) (Math.cos(a) * f5);
            float y = f4 - f5 + (float) (Math.sin(a) * f5);
            quadLine(m, vc, px, py, x, y, width, color);
            px = x;
            py = y;
        }
        quadLine(m, vc, px, py, f + f5, f2, width, color);
    }

    public static void renderRoundedRect(float f, float f2, float f3, float f4, float f5, Color color) {
        renderColoredRoundedRect(f, f2, f3, f4, f5, color, color, color, color);
    }

    public static void renderColoredRoundedRect(float f, float f2, float f3, float f4, float f5, Color topLeft, Color topRight, Color bottomLeft, Color bottomRight) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        int ctl = argb(topLeft);
        int ctr = argb(topRight);
        int cbl = argb(bottomLeft);
        int cbr = argb(bottomRight);
        // center quad
        v(m, vc, f + f5, f2 + f5, mix(ctl, cbl, 0.5f));
        v(m, vc, f + f5, f4 - f5, cbl);
        v(m, vc, f3 - f5, f4 - f5, mix(cbr, cbl, 0.5f));
        v(m, vc, f3 - f5, f2 + f5, ctr);
        // edge strips (bilinear gradient is linear along each edge)
        v(m, vc, f + f5, f2, ctl);
        v(m, vc, f + f5, f2 + f5, mix(ctl, cbl, 0.15f));
        v(m, vc, f3 - f5, f2 + f5, mix(ctr, cbr, 0.15f));
        v(m, vc, f3 - f5, f2, ctr);

        v(m, vc, f, f2 + f5, mix(ctl, cbl, 0.5f));
        v(m, vc, f, f4 - f5, mix(ctl, cbl, 0.85f));
        v(m, vc, f + f5, f4 - f5, cbl);
        v(m, vc, f + f5, f2 + f5, mix(ctl, cbl, 0.15f));

        v(m, vc, f3 - f5, f2 + f5, mix(ctr, cbr, 0.15f));
        v(m, vc, f3 - f5, f4 - f5, cbr);
        v(m, vc, f3, f4 - f5, mix(ctr, cbr, 0.85f));
        v(m, vc, f3, f2 + f5, mix(ctr, cbr, 0.5f));

        v(m, vc, f + f5, f4 - f5, cbl);
        v(m, vc, f + f5, f4, cbl);
        v(m, vc, f3 - f5, f4, cbr);
        v(m, vc, f3 - f5, f4 - f5, mix(cbr, cbl, 0.5f));
        // corner fans
        cornerFan(m, vc, f + f5, f2 + f5, f5, ctl, 2);
        cornerFan(m, vc, f3 - f5, f2 + f5, f5, ctr, 1);
        cornerFan(m, vc, f + f5, f4 - f5, f5, cbl, 3);
        cornerFan(m, vc, f3 - f5, f4 - f5, f5, cbr, 0);
    }

    private static int mix(int a, int b, float t) {
        int aa = a >>> 24, ar = a >> 16 & 0xFF, ag = a >> 8 & 0xFF, ab = a & 0xFF;
        int ba = b >>> 24, br = b >> 16 & 0xFF, bg = b >> 8 & 0xFF, bb = b & 0xFF;
        int ca = (int) (aa + (ba - aa) * t);
        int cr = (int) (ar + (br - ar) * t);
        int cg = (int) (ag + (bg - ag) * t);
        int cb = (int) (ab + (bb - ab) * t);
        return ca << 24 | cr << 16 | cg << 8 | cb;
    }

    /** quadrant: 0 = bottom-right, 1 = top-right, 2 = top-left, 3 = bottom-left */
    private static void cornerFan(Matrix3x2fStack m, net.minecraft.client.render.VertexConsumer vc, float cx, float cy, float r, int color, int quadrant) {
        float base = switch (quadrant) {
            case 0 -> 270f;
            case 1 -> 180f;
            case 2 -> 90f;
            default -> 0f;
        };
        int segs = 8;
        for (int i = 0; i < segs; i++) {
            double a1 = Math.toRadians(base - 90.0 * i / segs);
            double a2 = Math.toRadians(base - 90.0 * (i + 1) / segs);
            v(m, vc, cx, cy, color);
            v(m, vc, cx + (float) Math.cos(a1) * r, cy + (float) Math.sin(a1) * r, color);
            v(m, vc, cx + (float) Math.cos(a2) * r, cy + (float) Math.sin(a2) * r, color);
            v(m, vc, cx + (float) Math.cos(a2) * r, cy + (float) Math.sin(a2) * r, color);
        }
    }

    public static void renderGradient(float f, float f2, float f3, float f4, Color color, float f5) {
        Color white = new Color(255, 255, 255, (int) (f5 * 255));
        Color tinted = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (f5 * 255));
        renderColoredQuad(f, f2, f3, f4, white, tinted, white, tinted);
        renderColoredQuad(f, f2, f3, f4, new Color(0, 0, 0, 0), new Color(0, 0, 0, (int) (f5 * 255)), new Color(0, 0, 0, 0), new Color(0, 0, 0, (int) (f5 * 255)));
    }

    public static void renderFilledCircle(float f, float f2, float f3, Color color) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        int c = argb(color);
        for (int i = 0; i < 360; i += 5) {
            double a1 = Math.toRadians(i);
            double a2 = Math.toRadians(i + 5);
            v(m, vc, f, f2, c);
            v(m, vc, f + (float) (Math.sin(a1) * f3), f2 + (float) (Math.cos(a1) * f3), c);
            v(m, vc, f + (float) (Math.sin(a2) * f3), f2 + (float) (Math.cos(a2) * f3), c);
            v(m, vc, f + (float) (Math.sin(a2) * f3), f2 + (float) (Math.cos(a2) * f3), c);
        }
    }

    public static void renderCircularGradient(float f, float f2, float f3, float f4, Color color, float f5) {
        // top-left + bottom-right rounded corners connected with a diagonal band
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        int c = argb(color);
        int segs = 10;
        for (int i = 0; i < segs; i++) {
            double a1 = Math.PI / 2 + Math.PI * i / (2.0 * segs);
            double a2 = Math.PI / 2 + Math.PI * (i + 1) / (2.0 * segs);
            v(m, vc, f + f5 + (float) (Math.cos(a1) * f5), f2 + f5 + (float) (Math.sin(a1) * f5), c);
            v(m, vc, f + f5 + (float) (Math.cos(a2) * f5), f2 + f5 + (float) (Math.sin(a2) * f5), c);
            v(m, vc, f3, f4, c);
            v(m, vc, f3, f4, c);
        }
        for (int i = 0; i < segs; i++) {
            double a1 = Math.PI * i / (2.0 * segs);
            double a2 = Math.PI * (i + 1) / (2.0 * segs);
            v(m, vc, f3 - f5 + (float) (Math.cos(a1) * f5), f4 - f5 + (float) (Math.sin(a1) * f5), c);
            v(m, vc, f3 - f5 + (float) (Math.cos(a2) * f5), f4 - f5 + (float) (Math.sin(a2) * f5), c);
            v(m, vc, f + f5, f2 + f5, c);
            v(m, vc, f + f5, f2 + f5, c);
        }
    }

    public static void renderCrossed(float f, float f2, float f3, float f4, Color color) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        int c = argb(color);
        quadLine(m, vc, f, f2, f3, f4, 1.0f, c);
        quadLine(m, vc, f, f4, f3, f2, 1.0f, c);
    }

    public static void renderColoredEllipseBorder(float f, float f2, float f3, float f4, Color color, float f5) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        int c = argb(color);
        int segs = 10;
        for (int i = 0; i < segs; i++) {
            double a1 = Math.PI / 2 + Math.PI * i / (2.0 * segs);
            double a2 = Math.PI / 2 + Math.PI * (i + 1) / (2.0 * segs);
            v(m, vc, f + f5 + (float) (Math.cos(a1) * f5), f2 + f5 + (float) (Math.sin(a1) * f5), c);
            v(m, vc, f + f5 + (float) (Math.cos(a2) * f5), f2 + f5 + (float) (Math.sin(a2) * f5), c);
            v(m, vc, f3, f4, c);
            v(m, vc, f3, f4, c);
        }
        for (int i = 0; i < segs; i++) {
            double a1 = Math.PI + Math.PI * i / (2.0 * segs);
            double a2 = Math.PI + Math.PI * (i + 1) / (2.0 * segs);
            v(m, vc, f + f5 + (float) (Math.cos(a1) * f5), f4 - f5 + (float) (Math.sin(a1) * f5), c);
            v(m, vc, f + f5 + (float) (Math.cos(a2) * f5), f4 - f5 + (float) (Math.sin(a2) * f5), c);
            v(m, vc, f3, f4, c);
            v(m, vc, f3, f4, c);
        }
    }

    public static void renderColoredRectangleOutline(float f, float f2, float f3, float f4, Color color) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        int c = argb(color);
        quadLine(m, vc, f, f2, f, f4, 1.0f, c);
        quadLine(m, vc, f, f4, f3, f4, 1.0f, c);
        quadLine(m, vc, f3, f4, f3, f2, 1.0f, c);
        quadLine(m, vc, f3, f2, f, f2, 1.0f, c);
    }

    public static void renderArrows(float f, float f2, float f3, Color color) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        int c = argb(color);
        quadLine(m, vc, f, f2, f + 2.5f, f2 + 2.5f * f3, 1.0f, c);
        quadLine(m, vc, f + 5.0f, f2, f + 2.5f, f2 + 2.5f * f3, 1.0f, c);
    }

    /* ------------------------------------------------------------------ */
    /* gradient glow (replaces the old custom core shader)                 */
    /* ------------------------------------------------------------------ */

    public static void drawGradientGlow(float x, float y, float w, float h, float radius, float softness, Color c1, Color c2, Color c3, Color c4) {
        Matrix3x2fStack m = RenderHelper.getGuiMatrices();
        net.minecraft.client.render.VertexConsumer vc = guiBuffer();
        float r = Math.min(radius, Math.min(w, h) / 2.0f);
        int a0 = c1.getAlpha();
        if (softness > 0.01f) {
            int steps = MathHelper.clamp((int) Math.ceil(softness / 1.5f), 1, 14);
            for (int i = 0; i < steps; i++) {
                float tOut = 1.0f - (float) i / steps;
                float tIn = 1.0f - (float) (i + 1) / steps;
                int aOut = (int) (a0 * tOut * tOut);
                int aIn = (int) (a0 * tIn * tIn);
                int cOut = aOut << 24 | (c1.getRed() & 0xFF) << 16 | (c1.getGreen() & 0xFF) << 8 | (c1.getBlue() & 0xFF);
                int cIn = aIn << 24 | (c1.getRed() & 0xFF) << 16 | (c1.getGreen() & 0xFF) << 8 | (c1.getBlue() & 0xFF);
                glowBand(m, vc, x, y, w, h, r, softness * tOut, cOut);
                glowBand(m, vc, x, y, w, h, r, softness * tIn, cIn);
            }
        }
        renderColoredRoundedRect(x, y, x + w, y + h, r, c1, c3, c2, c4);
    }

    /** one thin quad ring at the given outset distance from the rounded rect */
    private static void glowBand(Matrix3x2fStack m, net.minecraft.client.render.VertexConsumer vc, float x, float y, float w, float h, float r, float outset, int color) {
        int segs = 10;
        float px = 0, py = 0;
        boolean first = true;
        // parametrize the contour: 4 arcs + implicit straight edges
        for (int q = 0; q < 4; q++) {
            for (int i = 0; i <= segs; i++) {
                double a;
                float cx, cy;
                switch (q) {
                    case 0 -> {
                        a = Math.PI * i / (2.0 * segs);
                        cx = x + w - r;
                        cy = y + r;
                    }
                    case 1 -> {
                        a = -Math.PI / 2 + Math.PI * i / (2.0 * segs);
                        cx = x + w - r;
                        cy = y + h - r;
                    }
                    case 2 -> {
                        a = Math.PI + Math.PI * i / (2.0 * segs);
                        cx = x + r;
                        cy = y + h - r;
                    }
                    default -> {
                        a = Math.PI / 2 + Math.PI * i / (2.0 * segs);
                        cx = x + r;
                        cy = y + r;
                    }
                }
                float nx = (float) Math.cos(a);
                float ny = (float) Math.sin(a);
                float ox = cx + nx * (r + outset);
                float oy = cy + ny * (r + outset);
                if (!first) {
                    v(m, vc, px + nx * 0.8f, py + ny * 0.8f, color);
                    v(m, vc, ox + nx * 0.8f, oy + ny * 0.8f, color);
                    v(m, vc, ox, oy, color);
                    v(m, vc, px, py, color);
                }
                px = ox;
                py = oy;
                first = false;
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /* shader rect (legacy entry point used by Interface)                  */
    /* ------------------------------------------------------------------ */

    public static void renderShaderRect(MatrixStack matrixStack, Color color, Color color2, Color color3, Color color4, float f, float f2, float f3, float f4, float f5, float f6) {
        if (shader == null) {
            return;
        }
        shader.setParameters(f, f2, f3, f4, f5, f6, color, color2, color3, color4);
        shader.use();
    }

    /* ------------------------------------------------------------------ */
    /* world rendering (3D)                                                */
    /* ------------------------------------------------------------------ */

    public static void renderFilledBox(float f, float f2, float f3, float f4, float f5, float f6, Color color) {
        MatrixStack matrixStack = RenderHelper.getMatrixStack();
        Matrix4f mat = matrixStack == null ? new Matrix4f() : matrixStack.peek().getPositionMatrix();
        net.minecraft.client.render.VertexConsumer vc = RenderHelper.immediate.getBuffer(RenderLayers.debugQuads());
        int c = argb(color);
        // 6 faces
        face(mat, vc, f, f2, f3, f, f5, f3, f4, f5, f3, f4, f2, f3, c);
        face(mat, vc, f, f2, f6, f4, f2, f6, f4, f5, f6, f, f5, f6, c);
        face(mat, vc, f, f2, f3, f, f2, f6, f4, f2, f6, f4, f2, f3, c);
        face(mat, vc, f, f5, f3, f4, f5, f3, f4, f5, f6, f, f5, f6, c);
        face(mat, vc, f, f2, f3, f4, f2, f3, f4, f5, f3, f, f5, f3, c);
        face(mat, vc, f, f2, f6, f, f5, f6, f4, f5, f6, f4, f2, f6, c);
    }

    private static void face(Matrix4f mat, net.minecraft.client.render.VertexConsumer vc, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int c) {
        v(mat, vc, x1, y1, z1, c);
        v(mat, vc, x2, y2, z2, c);
        v(mat, vc, x3, y3, z3, c);
        v(mat, vc, x4, y4, z4, c);
    }

    public static void renderOutlinedBox(float f, float f2, float f3, float f4, float f5, float f6, Color color) {
        MatrixStack matrixStack = RenderHelper.getMatrixStack();
        Matrix4f mat = matrixStack == null ? new Matrix4f() : matrixStack.peek().getPositionMatrix();
        net.minecraft.client.render.VertexConsumer vc = RenderHelper.immediate.getBuffer(RenderLayers.LINES);
        int c = argb(color);
        float[][] pts = {
                {f, f2, f3}, {f, f2, f6}, {f4, f2, f6}, {f4, f2, f3},
                {f, f5, f3}, {f, f5, f6}, {f4, f5, f6}, {f4, f5, f3}
        };
        int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
        for (int[] e : edges) {
            float[] a = pts[e[0]];
            float[] b = pts[e[1]];
            float dx = b[0] - a[0], dy = b[1] - a[1], dz = b[2] - a[2];
            float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len < 1.0E-5) continue;
            float nx = dx / len, ny = dy / len, nz = dz / len;
            vc.vertex(mat, a[0], a[1], a[2]).color(c).normal(nx, ny, nz);
            vc.vertex(mat, b[0], b[1], b[2]).color(c).normal(nx, ny, nz);
        }
    }

    public static void renderColoredEllipse3D(float f, float f2, float f3, float f4, Color color) {
        MatrixStack matrixStack = RenderHelper.getMatrixStack();
        Matrix4f mat = matrixStack == null ? new Matrix4f() : matrixStack.peek().getPositionMatrix();
        net.minecraft.client.render.VertexConsumer vc = RenderHelper.immediate.getBuffer(RenderLayers.LINES);
        int c = argb(color);
        float px = f + f4, pz = f3;
        for (int i = 5; i <= 100; i += 5) {
            double a = Math.PI * 2 * i / 100.0;
            float nx = f + f4 * (float) Math.cos(a);
            float nz = f3 + f4 * (float) Math.sin(a);
            float dx = nx - px, dz = nz - pz;
            float len = (float) Math.sqrt(dx * dx + dz * dz);
            if (len > 1.0E-5) {
                float nX = dx / len, nZ = dz / len;
                vc.vertex(mat, px, f2, pz).color(c).normal(nX, 0, nZ);
                vc.vertex(mat, nx, f2, nz).color(c).normal(nX, 0, nZ);
            }
            px = nx;
            pz = nz;
        }
    }

    public static void renderLines(ArrayList<Vec3d> arrayList, Color color) {
        MatrixStack matrixStack = RenderHelper.getMatrixStack();
        Matrix4f mat = matrixStack == null ? new Matrix4f() : matrixStack.peek().getPositionMatrix();
        net.minecraft.client.render.VertexConsumer vc = RenderHelper.immediate.getBuffer(RenderLayers.LINES);
        int c = argb(color);
        Vec3d prev = null;
        for (Vec3d vec3d : arrayList) {
            if (prev != null) {
                float dx = (float) (vec3d.x - prev.x);
                float dy = (float) (vec3d.y - prev.y);
                float dz = (float) (vec3d.z - prev.z);
                float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (len > 1.0E-5) {
                    vc.vertex(mat, (float) prev.x, (float) prev.y, (float) prev.z).color(c).normal(dx / len, dy / len, dz / len);
                    vc.vertex(mat, (float) vec3d.x, (float) vec3d.y, (float) vec3d.z).color(c).normal(dx / len, dy / len, dz / len);
                }
            }
            prev = vec3d;
        }
    }

    public static void setScissorRegion(float f, float f2, float f3, float f4) {
        net.minecraft.client.gui.screen.Screen screen = MinecraftClient.getInstance().currentScreen;
        int n;
        if (screen == null) {
            n = 0;
        } else {
            n = screen.height - (int) f4;
        }
        double d = MinecraftClient.getInstance().getWindow().getScaleFactor();
        GL11.glScissor((int) (f * d), (int) (n * d), (int) ((f3 - f) * d), (int) ((f4 - f2) * d));
        GL11.glEnable(3089);
    }

    public static void setCameraAction() {
        net.minecraft.client.render.Camera camera = MinecraftClient.getInstance().getEntityRenderDispatcher().camera;
        if (camera != null) {
            MatrixStack matrixStack = RenderHelper.getMatrixStack();
            matrixStack.push();
            Vec3d vec3d = camera.getPos();
            matrixStack.translate(-vec3d.x, -vec3d.y, -vec3d.z);
        }
    }

    public static Color getThemeColor(Color color, int n, int n2) {
        float[] fArray = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), fArray);
        float f = Math.abs(((float) (System.currentTimeMillis() % 2000) / 1000 + (float) n / (float) n2 * 2) % 2 - 1);
        fArray[2] = 0.25f + 0.75f * f % 2;
        return new Color(Color.HSBtoRGB(fArray[0], fArray[1], fArray[2]));
    }

    public static Color getColor(int n, float f) {
        return new Color((float) (14 + n) / 255, (float) (14 + n) / 255, (float) (14 + n) / 255, MathHelper.clamp(f, 0, 1));
    }

    public static Color getColor(Color color, float f) {
        return new Color((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, MathHelper.clamp(f, 0, 1));
    }

    public static Color getColor(float f, float f2) {
        return new Color(f, f, f, MathHelper.clamp(f2, 0, 1));
    }

    public static Vec3d worldSpaceToScreenSpace(Vec3d pos) {
        MinecraftClient mc = MinecraftClient.getInstance();
        net.minecraft.client.render.Camera camera = mc.getEntityRenderDispatcher().camera;
        int displayHeight = mc.getWindow().getHeight();
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Vector3f target = new Vector3f();

        double deltaX = pos.x - camera.getPos().x;
        double deltaY = pos.y - camera.getPos().y;
        double deltaZ = pos.z - camera.getPos().z;

        Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(RenderHelper.getPositionMatrix());

        Matrix4f matrixProj = new Matrix4f(RenderHelper.getProjectionMatrix());
        Matrix4f matrixModel = new Matrix4f(RenderHelper.getModelViewMatrix());

        matrixProj.mul(matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);

        return new Vec3d(target.x / mc.getWindow().getScaleFactor(), (displayHeight - target.y) / mc.getWindow().getScaleFactor(), target.z);
    }

    public static Vec3d getEntityPos(Entity entity) {
        double d = lerpTickDelta(entity.getX(), entity.lastX);
        double d2 = lerpTickDelta(entity.getY(), entity.lastY);
        double d3 = lerpTickDelta(entity.getZ(), entity.lastZ);
        return new Vec3d(d, d2, d3);
    }

    private static double lerpTickDelta(double d, double d2) {
        return d2 + (d - d2) * getTickDelta();
    }
}
