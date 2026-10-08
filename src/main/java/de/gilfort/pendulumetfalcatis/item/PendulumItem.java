package de.gilfort.pendulumetfalcatis.item;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;

/** The pendulum ("Pendel"): blocks attacks like a vanilla shield, improved by its core. */
public class PendulumItem extends ArcaneToolItem {
    /** Same durability as a vanilla shield. */
    public static final int BASE_DURABILITY = 336;

    public PendulumItem(Properties properties) {
        // Blocking values mirror the vanilla shield.
        super(properties.delayedComponent(DataComponents.BLOCKS_ATTACKS, context -> new BlocksAttacks(
                0.25F,
                1.0F,
                List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                Optional.of(SoundEvents.SHIELD_BLOCK),
                Optional.of(SoundEvents.SHIELD_BREAK))), BASE_DURABILITY);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    protected void applyTierStats(ItemStack stack, @Nullable CoreTier tier) {
        BlocksAttacks blocking = stack.get(DataComponents.BLOCKS_ATTACKS);
        if (blocking != null) {
            stack.set(DataComponents.BLOCKS_ATTACKS, new BlocksAttacks(
                    blocking.blockDelaySeconds(),
                    tier == null ? 1.0F : tier.shieldDisableScale(),
                    blocking.damageReductions(),
                    blocking.itemDamage(),
                    blocking.bypassedBy(),
                    blocking.blockSound(),
                    blocking.disableSound()));
        }
    }

    @Override
    protected InteractionResult useNormally(Level level, Player player, InteractionHand hand) {
        if (isInactive(player.getItemInHand(hand))) {
            return InteractionResult.PASS;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }
}
