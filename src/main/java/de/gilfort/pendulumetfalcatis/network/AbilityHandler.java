package de.gilfort.pendulumetfalcatis.network;

import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.jspecify.annotations.Nullable;

import de.gilfort.pendulumetfalcatis.card.ActiveEffect;
import de.gilfort.pendulumetfalcatis.card.TarotCard;
import de.gilfort.pendulumetfalcatis.card.ToolKind;
import de.gilfort.pendulumetfalcatis.card.ToolPassives;
import de.gilfort.pendulumetfalcatis.item.ArcaneToolItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Server side of the ability keys: validates the request and runs the active card's effect. */
public final class AbilityHandler {
    /** Minimum time between two abilities of the same tool, so a held key cannot drain durability every tick. */
    public static final int COOLDOWN_TICKS = 10;

    /** Game time of each player's last ability per tool. Players are weak keys, so logouts clean up. */
    private static final Map<Player, Map<ToolKind, Long>> LAST_USE = new WeakHashMap<>();

    private AbilityHandler() {
    }

    public static void handle(UseAbilityPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            useAbility(player, payload.tool());
        }
    }

    public static void useAbility(ServerPlayer player, ToolKind kind) {
        if (player.isSpectator() || !player.isAlive()) {
            return;
        }
        InteractionHand hand = findHand(player, kind);
        if (hand == null) {
            return;
        }
        ItemStack tool = player.getItemInHand(hand);
        TarotCard card = ToolPassives.getActiveCard(tool);
        if (card == null) {
            return;
        }

        ServerLevel level = (ServerLevel) player.level();
        long now = level.getGameTime();
        Map<ToolKind, Long> lastUse = LAST_USE.computeIfAbsent(player, p -> new EnumMap<>(ToolKind.class));
        if (now - lastUse.getOrDefault(kind, Long.MIN_VALUE / 2) < COOLDOWN_TICKS) {
            return;
        }

        ActiveEffect effect = card.effectsFor(kind).active();
        if (effect.activate(new ActiveEffect.Context(player, level, tool, hand))) {
            lastUse.put(kind, now);
            tool.hurtAndBreak(effect.durabilityCost(), player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
    }

    /** The hand holding a tool of the given kind; the main hand wins if both do. */
    private static @Nullable InteractionHand findHand(Player player, ToolKind kind) {
        for (InteractionHand hand : InteractionHand.values()) {
            if (player.getItemInHand(hand).getItem() instanceof ArcaneToolItem item && item.kind() == kind && item.worksInHand(hand)) {
                return hand;
            }
        }
        return null;
    }
}
