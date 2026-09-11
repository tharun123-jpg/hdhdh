/*
https://doxbin.com/upload/browniexcodez
*/
package dev.zprestige.prestige.client.util.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

public class RenderHelper {
    public static Matrix4f projectionMatrix = new Matrix4f();
    public static Matrix4f modelViewMatrix = new Matrix4f();
    public static Matrix4f positionMatrix = new Matrix4f();
    public static DrawContext context;
    public static MatrixStack matrixStack;
    public static Matrix3x2fStack guiMatrices;
    public static final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(new BufferAllocator(2 << 20));

    public static Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public static Matrix4f getModelViewMatrix() {
        return modelViewMatrix;
    }

    public static Matrix4f getPositionMatrix() {
        return positionMatrix;
    }

    public static DrawContext getContext() {
        return context;
    }

    public static void setContext(DrawContext context) {
        RenderHelper.context = context;
    }

    public static MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public static void setMatrixStack(MatrixStack matrixStack) {
        RenderHelper.matrixStack = matrixStack;
    }

    public static Matrix3x2fStack getGuiMatrices() {
        return guiMatrices;
    }

    public static void setGuiMatrices(Matrix3x2fStack guiMatrices) {
        RenderHelper.guiMatrices = guiMatrices;
    }

    // transformed point output for gui matrices (zero-alloc, render thread only)
    public static float tX;
    public static float tY;

    public static void transformPoint(Matrix3x2f m, float x, float y) {
        tX = m.m00 * x + m.m10 * y + m.m20;
        tY = m.m01 * x + m.m11 * y + m.m21;
    }
}
