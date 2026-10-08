package de.gilfort.pendulumetfalcatis.card;

import java.util.function.BiConsumer;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The effect of a tarot card in a tool's passive slot. All hooks are optional.
 * <p>
 * Passive effects only work while the tool is held: the scythe in the main hand, the pendulum
 * in either hand. Copies of the same card in one tool are combined into a single call whose
 * {@code strength} adds up with diminishing returns (1 + 0.5 + 0.25 + 0.125).
 */
public interface PassiveEffect {
    PassiveEffect NONE = new PassiveEffect() {
    };

    /**
     * Adds attribute modifiers while the tool is held. Modifier ids must be unique per card;
     * scale the amount with {@code strength}.
     */
    default void addAttributeModifiers(BiConsumer<Holder<Attribute>, AttributeModifier> modifiers, float strength) {
    }

    /** Adjusts damage the holder deals to {@code target}. Server side only. */
    default float modifyOutgoingDamage(Context context, LivingEntity target, DamageSource source, float damage) {
        return damage;
    }

    /** Adjusts damage the holder takes. Server side only. */
    default float modifyIncomingDamage(Context context, DamageSource source, float damage) {
        return damage;
    }

    /** Called after the holder blocked an attack with the pendulum. Server side only. */
    default void onBlock(Context context, DamageSource source, float blockedDamage) {
    }

    /** Called once per second while the tool is held. Server side only. */
    default void tick(Context context) {
    }

    record Context(Player player, ItemStack tool, InteractionHand hand, float strength) {
    }
}
