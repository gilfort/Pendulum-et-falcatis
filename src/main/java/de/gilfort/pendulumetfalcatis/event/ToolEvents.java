package de.gilfort.pendulumetfalcatis.event;

import de.gilfort.pendulumetfalcatis.item.ArcaneToolItem;
import de.gilfort.pendulumetfalcatis.item.PendulumItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

/** Game-bus listeners for the scythe and pendulum. */
public final class ToolEvents {
    private ToolEvents() {
    }

    /** A pendulum that ran out of durability while raised stops blocking. */
    @SubscribeEvent
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        ItemStack used = event.getEntity().getUseItem();
        if (used.getItem() instanceof PendulumItem && ArcaneToolItem.isInactive(used)) {
            event.setBlocked(false);
        }
    }
}
