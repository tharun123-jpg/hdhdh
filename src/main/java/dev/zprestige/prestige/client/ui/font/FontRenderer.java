package dev.zprestige.prestige.client.ui.font;

import dev.zprestige.prestige.client.Prestige;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.commons.codec.binary.Base64;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

//#SKIDDED
public class FontRenderer {
    private final float fontSize;
    private final int startChar;
    private final int endChar;
    private final float[] xPos;
    private final float[] yPos;
    private Font font;
    private Graphics2D graphics;
    private FontMetrics metrics;
    private BufferedImage bufferedImage;
    private Identifier resourceLocation;

    public FontRenderer(InputStream font) {
        this(font, 18F);
    }

    public FontRenderer(InputStream font, float size) {
        this.fontSize = size;
        this.startChar = 32;
        this.endChar = 255;
        this.xPos = new float[this.endChar - this.startChar];
        this.yPos = new float[this.endChar - this.startChar];
        setupGraphics2D();
        createFont(font, size);
    }

    private static NativeImage readTexture(String textureBase64) {
        try {
            byte[] imgBytes = Base64.decodeBase64(textureBase64);
            ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes);
            return NativeImage.read(bais);
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    private void setupGraphics2D() {
        this.bufferedImage = new BufferedImage(256, 256, 2);
        this.graphics = ((Graphics2D) this.bufferedImage.getGraphics());
        this.graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        this.graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    }

    private void createFont(InputStream font, float size) {
        try {
            this.font = Font.createFont(0, font).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.graphics.setFont(this.font);
        this.graphics.setColor(new Color(255, 255, 255, 0));
        this.graphics.fillRect(0, 0, 256, 256);
        this.graphics.setColor(Color.white);
        this.metrics = this.graphics.getFontMetrics();

        float x = 5.0F;
        float y = 5.0F;
        for (int i = this.startChar; i < this.endChar; i++) {
            this.graphics.drawString(Character.toString((char) i), x, y + this.metrics.getAscent());
            this.xPos[(i - this.startChar)] = x;
            this.yPos[(i - this.startChar)] = (y - this.metrics.getMaxDescent());
            x += this.metrics.stringWidth(Character.toString((char) i)) + 2.0F;
            if (x >= 250 - this.metrics.getMaxAdvance()) {
                x = 5.0F;
                y += this.metrics.getMaxAscent() + this.metrics.getMaxDescent() + this.fontSize / 2.0F;
            }
        }
        String base64 = imageToBase64String(bufferedImage);
        this.setResourceLocation(base64);
    }

    private String imageToBase64String(BufferedImage image) {
        String ret;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bos);
            byte[] bytes = bos.toByteArray();
            Base64 encoder = new Base64();
            ret = encoder.encodeAsString(bytes);
            ret = ret.replace(System.lineSeparator(), "");
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return ret;
    }

    public void setResourceLocation(String base64) {
        NativeImage image = readTexture(base64);
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                imgNew.setColor(x, y, image.getColor(x, y));
            }
        }

        image.close();
        this.resourceLocation = Identifier.of("prestige", "font/font.ttf");
        applyTexture(resourceLocation, imgNew);
    }

    private void applyTexture(Identifier identifier, NativeImage nativeImage) {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(() -> "prestige-font", nativeImage)));
    }

    public void drawString(Matrix3x2fStack matrices, String text, float x, float y, Color color, Color color2, boolean idk) {
        matrices.pushMatrix();
        matrices.scale(0.5f, 0.5f);
        if (idk) {
            this.drawer(matrices, text, x + 0.5f, y + 0.5f, color2);
        }
        this.drawer(matrices, text, x, y, color);
        matrices.popMatrix();
    }

    public void drawString(net.minecraft.client.util.math.MatrixStack matrixStack, String text, float x, float y, Color color, Color color2, boolean idk) {
        // world-space text (3D matrices), used by ESP
        this.drawer3D(matrixStack.peek().getPositionMatrix(), text, x, y, color);
    }

    public void drawString(String text, float x, float y, Color color) {
        this.drawString(text, x, y, color, true);
    }

    private void drawString(String text, float x, float y, Color color, boolean idk) {
        int n = Math.min(187, color.getAlpha());
        Matrix3x2fStack matrices = Prestige.Companion.getFontManager().getMatrixStack();
        this.drawString(matrices, text, x, y, color, new Color(0, 0, 0, n == -1 ? color.getAlpha() : n), idk);
    }

    private void drawer(Matrix3x2fStack matrices, String text, float x, float y, Color color) {
        StringBuilder finalText = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= this.startChar && c <= this.endChar) finalText.append(c);
            else finalText.append("?");
        }
        text = finalText.toString();
        x *= 2.0F;
        y *= 2.0F;
        org.joml.Matrix3x2f snapshot = new org.joml.Matrix3x2f(matrices);
        var vc = dev.zprestige.prestige.client.util.impl.RenderHelper.immediate.getBuffer(RenderLayers.text(this.resourceLocation));
        int argb = (color.getAlpha() & 0xFF) << 24 | (color.getRed() & 0xFF) << 16 | (color.getGreen() & 0xFF) << 8 | (color.getBlue() & 0xFF);
        for (int i = 0; i < text.length(); i++) {
            try {
                char c = text.charAt(i);
                drawChar(snapshot, vc, c, x, y, argb);
                x += getStringWidth(Character.toString(c)) * 2.0F;
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        }
    }

    private void drawer3D(Matrix4f matrix4f, String text, float x, float y, Color color) {
        StringBuilder finalText = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= this.startChar && c <= this.endChar) finalText.append(c);
            else finalText.append("?");
        }
        text = finalText.toString();
        x *= 2.0F;
        y *= 2.0F;
        var vc = dev.zprestige.prestige.client.util.impl.RenderHelper.immediate.getBuffer(RenderLayers.text(this.resourceLocation));
        int argb = (color.getAlpha() & 0xFF) << 24 | (color.getRed() & 0xFF) << 16 | (color.getGreen() & 0xFF) << 8 | (color.getBlue() & 0xFF);
        for (int i = 0; i < text.length(); i++) {
            try {
                char c = text.charAt(i);
                drawChar3D(matrix4f, vc, c, x, y, argb);
                x += getStringWidth(Character.toString(c)) * 2.0F;
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        }
    }

    public final float getStringWidth(String text) {
        return (float) (getBounds(text).getWidth()) / 2.0F;
    }

    public float getStringHeight() {
        return (float) this.getBounds("W").getHeight() / 2.0f;
    }

    private Rectangle2D getBounds(String text) {
        return this.metrics.getStringBounds(text, this.graphics);
    }

    private void drawChar(org.joml.Matrix3x2f m, net.minecraft.client.render.VertexConsumer vc, char character, float x, float y, int argb) throws ArrayIndexOutOfBoundsException {
        Rectangle2D bounds = this.metrics.getStringBounds(Character.toString(character), this.graphics);
        drawTexturedModalRect(m, vc, x, y, this.xPos[(character - this.startChar)], this.yPos[(character - this.startChar)], (float) bounds.getWidth(), (float) bounds.getHeight() + this.metrics.getMaxDescent() + 1.0F, argb);
    }

    private void drawChar3D(Matrix4f m, net.minecraft.client.render.VertexConsumer vc, char character, float x, float y, int argb) throws ArrayIndexOutOfBoundsException {
        Rectangle2D bounds = this.metrics.getStringBounds(Character.toString(character), this.graphics);
        drawTexturedModalRect3D(m, vc, x, y, this.xPos[(character - this.startChar)], this.yPos[(character - this.startChar)], (float) bounds.getWidth(), (float) bounds.getHeight() + this.metrics.getMaxDescent() + 1.0F, argb);
    }

    private void drawTexturedModalRect(org.joml.Matrix3x2f m, net.minecraft.client.render.VertexConsumer vc, float x, float y, float u, float v, float width, float height, int argb) {
        float scale = 0.0039063F;
        vc.vertex(m, x, y + height).color(argb).texture(u * scale, (v + height) * scale).light(dev.zprestige.prestige.client.util.impl.RenderUtil.FULL_LIGHT);
        vc.vertex(m, x + width, y + height).color(argb).texture((u + width) * scale, (v + height) * scale).light(dev.zprestige.prestige.client.util.impl.RenderUtil.FULL_LIGHT);
        vc.vertex(m, x + width, y).color(argb).texture((u + width) * scale, v * scale).light(dev.zprestige.prestige.client.util.impl.RenderUtil.FULL_LIGHT);
        vc.vertex(m, x, y).color(argb).texture(u * scale, v * scale).light(dev.zprestige.prestige.client.util.impl.RenderUtil.FULL_LIGHT);
    }

    private void drawTexturedModalRect3D(Matrix4f m, net.minecraft.client.render.VertexConsumer vc, float x, float y, float u, float v, float width, float height, int argb) {
        float scale = 0.0039063F;
        vc.vertex(m, x, y + height, 0.0f).color(argb).texture(u * scale, (v + height) * scale).light(dev.zprestige.prestige.client.util.impl.RenderUtil.FULL_LIGHT);
        vc.vertex(m, x + width, y + height, 0.0f).color(argb).texture((u + width) * scale, (v + height) * scale).light(dev.zprestige.prestige.client.util.impl.RenderUtil.FULL_LIGHT);
        vc.vertex(m, x + width, y, 0.0f).color(argb).texture((u + width) * scale, v * scale).light(dev.zprestige.prestige.client.util.impl.RenderUtil.FULL_LIGHT);
        vc.vertex(m, x, y, 0.0f).color(argb).texture(u * scale, v * scale).light(dev.zprestige.prestige.client.util.impl.RenderUtil.FULL_LIGHT);
    }
}

