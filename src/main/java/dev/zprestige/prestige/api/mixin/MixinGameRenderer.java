package dev.zprestige.prestige.api.mixin;

import dev.zprestige.prestige.client.Prestige;
import dev.zprestige.prestige.client.event.impl.FloatingItemEvent;
import dev.zprestige.prestige.client.event.impl.ReachEvent;
import dev.zprestige.prestige.client.event.impl.Render3DEvent;
import dev.zprestige.prestige.client.event.impl.TiltEvent;
import dev.zprestige.prestige.client.util.impl.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = { GameRenderer.class }, priority = 999)
public class MixinGameRenderer
{
    @Shadow
    @Final
    public MinecraftClient client;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updateCrosshairTarget(float tickDelta) {
        Entity entity = client.getCameraEntity();
        if (entity == null || client.world == null) {
            return;
        }
        client.targetedEntity = null;
        double n2 = entity instanceof LivingEntity livingEntity ? livingEntity.getAttributeValue(EntityAttributes.ENTITY_INTERACTION_RANGE) : 3.0;
        boolean invoke = false;
        if (!Prestige.Companion.getSelfDestructed()) {
            ReachEvent event = new ReachEvent(0);
            if (event.invoke()) {
                n2 += event.getReach();
            }
        }
        client.crosshairTarget = entity.raycast(n2, tickDelta, false);
        Vec3d cameraPosVec = entity.getCameraPosVec(tickDelta);
        boolean b = false;
        double n3 = n2;
        double n4;
        if (!client.player.isCreative()) {
            if (n3 > 3.0 && !invoke) {
                b = true;
            }
            n4 = n3;
        } else {
            n3 = (n4 = 6.0);
        }
        double squaredDistanceTo = n3 * n3;
        if (client.crosshairTarget != null) {
            squaredDistanceTo = client.crosshairTarget.getPos().squaredDistanceTo(cameraPosVec);
        }
        Vec3d vec = entity.getRotationVec(1);
        EntityHitResult raycast = ProjectileUtil.raycast(entity, cameraPosVec, cameraPosVec.add(vec.x * n4, vec.y * n4, vec.z * n4), entity.getBoundingBox().stretch(vec.multiply(n4)).expand(1.0, 1.0, 1.0), e -> !e.isSpectator() && e.canHit(), squaredDistanceTo);
        if (raycast != null) {
            double d3 = cameraPosVec.squaredDistanceTo(raycast.getPos());
            if (b && d3 > 9.0) {
                client.crosshairTarget = BlockHitResult.createMissed(raycast.getPos(), Direction.getFacing(vec.x, vec.y, vec.z), BlockPos.ofFloored(raycast.getPos()));
            } else if (d3 < squaredDistanceTo || client.crosshairTarget == null) {
                client.crosshairTarget = raycast;
                if (raycast.getEntity() instanceof LivingEntity || raycast.getEntity() instanceof ItemFrameEntity) {
                    client.targetedEntity = raycast.getEntity();
                }
            }
        }
    }

    @Shadow
    public Matrix4f getBasicProjectionMatrix(float f) {
        throw new AssertionError();
    }

    @Unique
    private MatrixStack prestige$cameraStack(RenderTickCounter tickCounter) {
        MatrixStack matrixStack = new MatrixStack();
        var camera = client.gameRenderer.getCamera();
        matrixStack.multiply(camera.getRotation());
        Vec3d pos = camera.getCameraPos();
        matrixStack.translate(-pos.x, -pos.y, -pos.z);
        return matrixStack;
    }

    @Unique
    private void prestige$captureMatrices(RenderTickCounter tickCounter, MatrixStack matrixStack) {
        float tickDelta = tickCounter.getTickProgress(false);
        RenderHelper.getModelViewMatrix().set(matrixStack.peek().getPositionMatrix());
        RenderHelper.getPositionMatrix().set(matrixStack.peek().getPositionMatrix());
        // rebuild an approximate vanilla projection matrix (fov * fov modifier)
        float fov = client.options.getFov().getValue();
        float modifier = 1.0f;
        if (client.player != null) {
            modifier = client.player.getFovMultiplier(true, tickDelta);
        }
        Matrix4f projection = getBasicProjectionMatrix(fov * modifier);
        RenderHelper.getProjectionMatrix().set(projection);
    }

    @Inject(at = { @At("HEAD") }, method = { "renderWorld" })
    void render3dHook(RenderTickCounter tickCounter, CallbackInfo callbackInfo) {
        if (!Prestige.Companion.getSelfDestructed()) {
            MatrixStack matrixStack = prestige$cameraStack(tickCounter);
            RenderHelper.setMatrixStack(matrixStack);
            prestige$captureMatrices(tickCounter, matrixStack);
        }
    }

    @Inject(at = { @At("HEAD") }, method = { "renderWorld" }, cancellable = true)
    void renderWorld(RenderTickCounter tickCounter, CallbackInfo callbackInfo) {
        if (!Prestige.Companion.getSelfDestructed()) {
            MatrixStack matrixStack = prestige$cameraStack(tickCounter);
            if (new Render3DEvent(matrixStack, tickCounter.getTickProgress(false)).invoke()) {
                dev.zprestige.prestige.client.util.impl.RenderUtil.flush();
                callbackInfo.cancel();
            }
        }
    }

    @Inject(at = { @At("HEAD") }, method = { "tiltViewWhenHurt" }, cancellable = true)
    void tiltViewWhenHurt(MatrixStack matrixStack, float n, CallbackInfo callbackInfo) {
        if (!Prestige.Companion.getSelfDestructed()) {
            if (new TiltEvent().invoke()) {
                callbackInfo.cancel();
            }
        }
    }
}
