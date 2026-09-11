package dev.zprestige.prestige.api.mixin;

import dev.zprestige.prestige.client.Prestige;
import dev.zprestige.prestige.client.event.impl.CrosshairEvent;
import dev.zprestige.prestige.client.event.impl.Render2DEvent;
import dev.zprestige.prestige.client.event.impl.StatusEffectOverlayEvent;
import dev.zprestige.prestige.client.util.impl.RenderHelper;
import dev.zprestige.prestige.client.util.impl.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={InGameHud.class})
public class MixinInGameHud {
    @Inject(at={@At(value="HEAD")}, method={"renderStatusEffectOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"}, cancellable=true)
    void renderStatusEffectOverlay(DrawContext drawContext, RenderTickCounter tickCounter, CallbackInfo callbackInfo) {
        if (Prestige.Companion.getSelfDestructed()) {
            return;
        }
        if (new StatusEffectOverlayEvent().invoke()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method={"renderCrosshair(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"}, at={@At(value="HEAD")}, cancellable=true)
    void renderCrosshair(DrawContext drawContext, RenderTickCounter tickCounter, CallbackInfo callbackInfo) {
        if (Prestige.Companion.getSelfDestructed()) {
            return;
        }
        if (new CrosshairEvent().invoke()) {
            callbackInfo.cancel();
        }
    }

    @Inject(at = { @At("HEAD") }, method = { "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V" }, cancellable = true)
    void render(DrawContext drawContext, RenderTickCounter tickCounter, CallbackInfo callbackInfo) {
        if (!Prestige.Companion.getSelfDestructed()) {
            RenderHelper.setContext(drawContext);
            Matrix3x2fStack matrices = drawContext.getMatrices();
            RenderHelper.setGuiMatrices(matrices);
            MinecraftClient mc = MinecraftClient.getInstance();
            int scaledWidth = mc.getWindow().getScaledWidth();
            int scaledHeight = mc.getWindow().getScaledHeight();
            if (!new Render2DEvent(matrices, scaledWidth, scaledHeight).invoke()) {
                if (!(mc.currentScreen instanceof dev.zprestige.prestige.client.ui.Interface)) {
                    RenderUtil.flush();
                    return;
                }
            }
            RenderUtil.flush();
            callbackInfo.cancel();
        }
    }
}
