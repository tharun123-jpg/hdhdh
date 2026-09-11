package dev.zprestige.prestige.api.mixin;

import dev.zprestige.prestige.client.Prestige;
import dev.zprestige.prestige.client.event.impl.FrustrumEvent;
import dev.zprestige.prestige.client.event.impl.LightEvent;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={WorldRenderer.class})
public class MixinWorldRenderer {

    @ModifyVariable(method={"getLightmapCoordinates(Lnet/minecraft/client/render/WorldRenderer$BrightnessGetter;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I"}, at=@At(value="STORE"), ordinal=0)
    private static int getLightmapCoordinatesModifySkyLight(int n) {
        if (Prestige.Companion.getSelfDestructed()) {
            return n;
        }
        LightEvent lightEvent = new LightEvent();
        if (lightEvent.invoke()) {
            return Math.max(15, n);
        }
        return n;
    }

    @Inject(at={@At(value="RETURN")}, method={"setupFrustum"})
    void onUpdateFrustum(Matrix4f matrix4f, Matrix4f matrix4f2, Vec3d vec3d, CallbackInfoReturnable<Frustum> callbackInfoReturnable) {
        if (Prestige.Companion.getSelfDestructed()) {
            return;
        }
        new FrustrumEvent(callbackInfoReturnable.getReturnValue()).invoke();
    }
}
