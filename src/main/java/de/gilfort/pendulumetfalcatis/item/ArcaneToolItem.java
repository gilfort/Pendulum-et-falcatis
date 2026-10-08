package de.gilfort.pendulumetfalcatis.item;

import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import de.gilfort.pendulumetfalcatis.menu.ToolMenu;
import de.gilfort.pendulumetfalcatis.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/**
 * Shared base for the scythe and the pendulum: both carry a core and tarot cards, which are
 * edited in the tool menu opened with sneak + use.
 * <p>
 * These tools never break. Durability stops at one remaining point, and the tool is then
 * "inactive" (no bonuses, no blocking, no abilities) until it is repaired.
 */
public abstract class ArcaneToolItem extends Item {
    private final int baseDurability;

    protected ArcaneToolItem(Properties properties, int baseDurability) {
        super(properties.durability(baseDurability).component(ModDataComponents.CORE_TIER.get(), CoreTier.BASIC));
        this.baseDurability = baseDurability;
    }

    /** The installed core, or {@code null} if the core was taken out. */
    public static @Nullable CoreTier getCoreTier(ItemStack stack) {
        return stack.get(ModDataComponents.CORE_TIER.get());
    }

    public static ItemContainerContents getCards(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CARDS.get(), ItemContainerContents.EMPTY);
    }

    public static boolean isWornOut(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    /** A tool without a core or without durability has no bonuses, cannot block and has no abilities. */
    public static boolean isInactive(ItemStack stack) {
        return getCoreTier(stack) == null || isWornOut(stack);
    }

    /**
     * Installs a core, or removes it when {@code tier} is {@code null}: stores the tier and applies
     * its durability and stats. A tool without a core keeps its base durability.
     */
    public void installCore(ItemStack stack, @Nullable CoreTier tier) {
        if (tier == null) {
            stack.remove(ModDataComponents.CORE_TIER.get());
        } else {
            stack.set(ModDataComponents.CORE_TIER.get(), tier);
        }
        // Keep the worn fraction, rounding up, so swapping cores back and forth never restores durability.
        boolean wornOut = isWornOut(stack);
        double wornFraction = (double) stack.getDamageValue() / stack.getMaxDamage();
        float multiplier = tier == null ? 1.0F : tier.durabilityMultiplier();
        int maxDamage = Math.round(baseDurability * multiplier);
        stack.set(DataComponents.MAX_DAMAGE, maxDamage);
        int damage = wornOut ? maxDamage - 1 : (int) Math.ceil(wornFraction * maxDamage);
        stack.setDamageValue(Math.min(damage, maxDamage - 1));
        applyTierStats(stack, tier);
    }

    /** Applies tier-dependent stats stored in components. Stats computed on the fly need no work here. */
    protected void applyTierStats(ItemStack stack, @Nullable CoreTier tier) {
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                ItemStack stack = player.getItemInHand(hand);
                player.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> new ToolMenu(containerId, inventory, hand),
                        stack.getHoverName()), buf -> buf.writeEnum(hand));
            }
            return InteractionResult.SUCCESS;
        }
        return useNormally(level, player, hand);
    }

    /** Use behaviour when not sneaking. */
    protected InteractionResult useNormally(Level level, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
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
        if (tier == null) {
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.no_core").withStyle(ChatFormatting.RED));
        } else {
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.core",
                    Component.translatable(tier.translationKey()), tier.level()).withStyle(ChatFormatting.GRAY));
        }
        if (isWornOut(stack)) {
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.inactive").withStyle(ChatFormatting.RED));
        }
        tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.open_menu").withStyle(ChatFormatting.DARK_GRAY));
    }
}
