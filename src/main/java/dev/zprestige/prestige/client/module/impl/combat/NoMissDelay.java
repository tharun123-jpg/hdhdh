package dev.zprestige.prestige.client.module.impl.combat;

import dev.zprestige.prestige.client.util.impl.ItemChecks;

import dev.zprestige.prestige.client.event.EventListener;
import dev.zprestige.prestige.client.event.impl.LastAttackedEvent;
import dev.zprestige.prestige.client.event.impl.SwingHandEvent;
import dev.zprestige.prestige.client.module.Category;
import dev.zprestige.prestige.client.module.Module;
import net.minecraft.item.AxeItem;
import net.minecraft.util.hit.HitResult;

public class NoMissDelay extends Module {

    public NoMissDelay() {
        super("No Miss Delay", Category.Combat, "Removes the sword delay after missing");
    }

    @EventListener
    public void event(SwingHandEvent event) {
        if (getMc().crosshairTarget != null) {
            if (ItemChecks.isSwordOrAxe(getMc().player.getMainHandStack())) {
                if (getMc().crosshairTarget.getType() == HitResult.Type.MISS) {
                    event.setCancelled();
                }
            }
        }
    }

    @EventListener
    public void event(LastAttackedEvent event) {
        if (getMc().crosshairTarget != null) {
            if (ItemChecks.isSwordOrAxe(getMc().player.getMainHandStack())) {
                if (getMc().crosshairTarget.getType() == HitResult.Type.MISS) {
                    event.setCancelled();
                }
            }
        }
    }
}