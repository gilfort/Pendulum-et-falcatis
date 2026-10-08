package de.gilfort.pendulumetfalcatis.item;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** A core that can be installed into a scythe or pendulum through the tool menu. */
public class CoreItem extends Item {
    private final CoreTier tier;

    public CoreItem(CoreTier tier, Properties properties) {
        super(properties.stacksTo(1));
        this.tier = tier;
    }

    public CoreTier tier() {
        return tier;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.core_tier", tier.level()).withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.passive_slots", tier.passiveSlots()).withStyle(ChatFormatting.GRAY));
    }
}
