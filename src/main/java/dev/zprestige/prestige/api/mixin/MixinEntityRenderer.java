package dev.zprestige.prestige.api.mixin;

import dev.zprestige.prestige.client.Prestige;
import dev.zprestige.prestige.client.event.impl.HealtEvent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class}, priority=999)
public abstract class MixinEntityRenderer<T extends Entity, S extends EntityRenderState> {

    @Inject(method={"render"}, at={@At(value="HEAD")})
    void render(S state, net.minecraft.client.util.math.MatrixStack matrixStack, net.minecraft.client.render.command.OrderedRenderCommandQueue queue, net.minecraft.client.render.state.CameraRenderState cameraRenderState, CallbackInfo callbackInfo) {
        if (Prestige.Companion.getSelfDestructed()) {
            return;
        }
        if (state instanceof PlayerEntityRenderState playerState) {
            ClientWorld world = Prestige.Companion.getSelfDestructed() ? null : (ClientWorld) net.minecraft.client.MinecraftClient.getInstance().world;
            if (world == null) {
                return;
            }
            Entity entity = world.getEntityById(playerState.id);
            if (entity instanceof AbstractClientPlayerEntity playerEntity && entity.isAlive()) {
                HealtEvent event = new HealtEvent(playerEntity, "");
                if (event.invoke()) {
                    playerState.displayName = Text.of(playerState.displayName.getString() + event.getText());
                }
            }
        }
        if (state.id != 0) {
            ClientWorld world = (ClientWorld) net.minecraft.client.MinecraftClient.getInstance().world;
            if (world != null) {
                Entity entity = world.getEntityById(state.id);
                if (entity != null) {
                    new dev.zprestige.prestige.client.event.impl.RenderHitboxEvent(entity).invoke();
                }
            }
        }
    }
}
