package dev.zprestige.prestige.client.event.impl;

import dev.zprestige.prestige.client.event.Event;
import org.joml.Matrix3x2fStack;

public class Render2DEvent extends Event {
    public Matrix3x2fStack matrixStack;
    public int scaledWidth;
    public int scaledHeight;

    public Render2DEvent(Matrix3x2fStack matrixStack, int scaledWidth, int scaledHeight) {
        this.matrixStack = matrixStack;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
    }

    public Matrix3x2fStack getMatrixStack() {
        return this.matrixStack;
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }
}
