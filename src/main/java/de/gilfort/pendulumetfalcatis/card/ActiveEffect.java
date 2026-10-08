package de.gilfort.pendulumetfalcatis.card;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/** The effect of a tarot card in a tool's active slot, triggered by the player's ability key. */
public interface ActiveEffect {
    ActiveEffect NONE = context -> false;

    /**
     * Runs the ability on the server.
     *
     * @return whether the ability fired; durability is only charged if it did
     */
    boolean activate(Context context);

    /** Durability the tool loses each time the ability fires. */
    default int durabilityCost() {
        return 1;
    }

    record Context(ServerPlayer player, ServerLevel level, ItemStack tool, InteractionHand hand) {
    }
}
