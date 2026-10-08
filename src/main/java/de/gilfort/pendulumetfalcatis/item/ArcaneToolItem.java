package de.gilfort.pendulumetfalcatis.item;

import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import de.gilfort.pendulumetfalcatis.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Shared base for the scythe and the pendulum: both carry a core and, later, tarot cards.
 * <p>
 * These tools never break. Durability stops at one remaining point, and the tool is then
 * "inactive" (no bonuses, no blocking, no abilities) until it is repaired.
 */
public abstract class ArcaneToolItem extends Item {
    protected ArcaneToolItem(Properties properties) {
        super(properties.component(ModDataComponents.CORE_TIER.get(), CoreTier.BASIC));
    }

    public static CoreTier getCoreTier(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CORE_TIER.get(), CoreTier.BASIC);
    }

    public static boolean isInactive(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<ItemStack> onBroken) {
        int remaining = stack.getMaxDamage() - 1 - stack.getDamageValue();
        return Math.max(0, Math.min(amount, remaining));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        CoreTier tier = getCoreTier(stack);
        tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.core",
                Component.translatable(tier.translationKey()), tier.level()).withStyle(ChatFormatting.GRAY));
        if (isInactive(stack)) {
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.inactive").withStyle(ChatFormatting.RED));
        }
    }
}
