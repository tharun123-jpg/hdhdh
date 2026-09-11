package dev.zprestige.prestige.api.mixin;

import dev.zprestige.prestige.client.Prestige;
import dev.zprestige.prestige.client.event.impl.KeyEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Keyboard.class})
public class MixinKeyboard {
    @Inject(method = { "onKey" }, at = { @At("HEAD") }, cancellable = true)
    void onKey(long window, int action, KeyInput input, CallbackInfo callbackInfo) {
        if (!Prestige.Companion.getSelfDestructed()) {
            if (input.getKeycode() != -1) {
                if (new KeyEvent(input.getKeycode(), action).invoke()) {
                    callbackInfo.cancel();
                }
            }
        }
    }
}
