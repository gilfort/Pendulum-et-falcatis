package de.gilfort.pendulumetfalcatis.item;

import java.util.function.Consumer;

import de.gilfort.pendulumetfalcatis.card.TarotCard;
import de.gilfort.pendulumetfalcatis.card.ToolKind;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** A tarot card item. Slotted into a scythe or pendulum, it provides that tool's effects of its {@link TarotCard}. */
public class TarotCardItem extends Item {
    private final TarotCard card;

    public TarotCardItem(TarotCard card, Properties properties) {
        super(properties.stacksTo(16));
        this.card = card;
    }

    public TarotCard card() {
        return card;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        for (ToolKind kind : ToolKind.values()) {
            String prefix = card.translationKey() + "." + kind.getSerializedName();
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.card." + kind.getSerializedName()).withStyle(ChatFormatting.GOLD));
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.card.active", Component.translatable(prefix + ".active")).withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.card.passive", Component.translatable(prefix + ".passive")).withStyle(ChatFormatting.GRAY));
        }
    }
}
