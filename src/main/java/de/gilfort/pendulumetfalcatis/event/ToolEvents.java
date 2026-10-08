package de.gilfort.pendulumetfalcatis.event;

import de.gilfort.pendulumetfalcatis.card.PassiveEffect;
import de.gilfort.pendulumetfalcatis.card.ToolPassives;
import de.gilfort.pendulumetfalcatis.item.ArcaneToolItem;
import de.gilfort.pendulumetfalcatis.item.PendulumItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Game-bus listeners for the scythe and pendulum, including the evaluation of passive card effects. */
public final class ToolEvents {
    private static final int PASSIVE_TICK_INTERVAL = 20;

    private ToolEvents() {
    }

    @SubscribeEvent
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        ItemStack used = event.getEntity().getUseItem();
        if (!(used.getItem() instanceof PendulumItem)) {
            return;
        }
        // A pendulum that became inactive while raised stops blocking.
        if (ArcaneToolItem.isInactive(used)) {
            event.setBlocked(false);
            return;
        }
        if (event.getBlocked() && event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            InteractionHand hand = player.getUsedItemHand();
            DamageSource source = event.getDamageSource();
            float blocked = event.getBlockedDamage();
            for (ToolPassives.Entry entry : ToolPassives.getPassives(used)) {
                entry.effect().onBlock(new PassiveEffect.Context(player, used, hand, entry.strength()), source, blocked);
            }
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();
        if (source.getEntity() instanceof Player attacker && attacker != target) {
            ToolPassives.forEachPassive(attacker, (effect, context) ->
                    event.setAmount(effect.modifyOutgoingDamage(context, target, source, event.getAmount())));
        }
        if (target instanceof Player victim) {
            ToolPassives.forEachPassive(victim, (effect, context) ->
                    event.setAmount(effect.modifyIncomingDamage(context, source, event.getAmount())));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide() && player.tickCount % PASSIVE_TICK_INTERVAL == 0) {
            ToolPassives.forEachPassive(player, PassiveEffect::tick);
        }
    }

    /** Adds the attribute modifiers of passive cards to the tool, active in the hands the tool works in. */
    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof ArcaneToolItem item)) {
            return;
        }
        EquipmentSlotGroup slots = item.worksInHand(InteractionHand.OFF_HAND) ? EquipmentSlotGroup.HAND : EquipmentSlotGroup.MAINHAND;
        for (ToolPassives.Entry entry : ToolPassives.getPassives(stack)) {
            entry.effect().addAttributeModifiers((attribute, modifier) -> event.addModifier(attribute, modifier, slots), entry.strength());
        }
    }
}
