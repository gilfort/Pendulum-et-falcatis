package de.gilfort.pendulumetfalcatis.item;

import java.util.function.Consumer;

import de.gilfort.pendulumetfalcatis.card.ActiveEffect;
import de.gilfort.pendulumetfalcatis.card.TarotCard;
import de.gilfort.pendulumetfalcatis.card.ToolKind;
import de.gilfort.pendulumetfalcatis.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/**
 * A tarot card item. Slotted into a scythe or pendulum, it provides that tool's effects of its {@link TarotCard};
 * used directly, it is consumed for its one-time reading. A card can be upright or reversed.
 */
public class TarotCardItem extends Item {
    private final TarotCard card;

    public TarotCardItem(TarotCard card, Properties properties) {
        super(properties.stacksTo(16));
        this.card = card;
    }

    public TarotCard card() {
        return card;
    }

    public static boolean isReversed(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.REVERSED.get(), false);
    }

    /** Turns the card upside down or back. The component is removed when upright, so upright cards stack with fresh ones. */
    public static void setReversed(ItemStack stack, boolean reversed) {
        if (reversed) {
            stack.set(ModDataComponents.REVERSED.get(), true);
        } else {
            stack.remove(ModDataComponents.REVERSED.get());
        }
    }

    /** Uses the card's one-time reading. The card is only consumed if the reading fired. */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);
            ActiveEffect reading = card.side(isReversed(stack)).reading();
            if (!reading.activate(new ActiveEffect.Context(serverPlayer, serverLevel, stack, hand))) {
                return InteractionResult.FAIL;
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        boolean reversed = isReversed(stack);
        String prefix = card.translationKey(reversed);
        if (reversed) {
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.card.reversed").withStyle(ChatFormatting.DARK_RED));
        }
        for (ToolKind kind : ToolKind.values()) {
            String toolPrefix = prefix + "." + kind.getSerializedName();
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.card." + kind.getSerializedName()).withStyle(ChatFormatting.GOLD));
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.card.active", Component.translatable(toolPrefix + ".active")).withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.card.passive", Component.translatable(toolPrefix + ".passive")).withStyle(ChatFormatting.GRAY));
        }
        tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.card.reading").withStyle(ChatFormatting.GOLD));
        tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.card.reading_effect", Component.translatable(prefix + ".reading")).withStyle(ChatFormatting.GRAY));
    }
}
