/*
zPrestige is a skidder lmao
*/
package dev.zprestige.prestige.client.managers;

import dev.zprestige.prestige.client.Prestige;
import dev.zprestige.prestige.client.ui.font.FontRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3x2fStack;

public class FontManager {
    public Matrix3x2fStack matrix;
    public MatrixStack worldMatrix;
    public FontRenderer fontRenderer = new FontRenderer(Prestige.class.getClassLoader().getResourceAsStream("assets/prestige/font/font.ttf"), 18);

    public Matrix3x2fStack getMatrixStack() {
        return this.matrix;
    }

    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    public void setMatrixStack(Matrix3x2fStack matrixStack) {
        this.matrix = matrixStack;
    }

    public MatrixStack getWorldMatrix() {
        return this.worldMatrix;
    }

    public void setWorldMatrix(MatrixStack matrixStack) {
        this.worldMatrix = matrixStack;
    }
}
