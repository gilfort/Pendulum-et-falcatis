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

    public static CoreTier getCoreTier(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CORE_TIER.get(), CoreTier.BASIC);
    }

    public static ItemContainerContents getCards(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CARDS.get(), ItemContainerContents.EMPTY);
    }

    public static boolean isInactive(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    /** Installs a core: stores the tier and applies the tier's durability and stats. */
    public void installCore(ItemStack stack, CoreTier tier) {
        stack.set(ModDataComponents.CORE_TIER.get(), tier);
        int maxDamage = Math.round(baseDurability * tier.durabilityMultiplier());
        stack.set(DataComponents.MAX_DAMAGE, maxDamage);
        if (stack.getDamageValue() > maxDamage - 1) {
            stack.setDamageValue(maxDamage - 1);
        }
        applyTierStats(stack, tier);
    }

    /** Applies tier-dependent stats stored in components. Stats computed on the fly need no work here. */
    protected void applyTierStats(ItemStack stack, CoreTier tier) {
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
        tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.core",
                Component.translatable(tier.translationKey()), tier.level()).withStyle(ChatFormatting.GRAY));
        if (isInactive(stack)) {
            tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.inactive").withStyle(ChatFormatting.RED));
        }
        tooltip.accept(Component.translatable("tooltip.pendulumetfalcatis.open_menu").withStyle(ChatFormatting.DARK_GRAY));
    }
}
