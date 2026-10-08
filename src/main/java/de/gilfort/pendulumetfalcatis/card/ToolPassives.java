package de.gilfort.pendulumetfalcatis.card;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import de.gilfort.pendulumetfalcatis.item.ArcaneToolItem;
import de.gilfort.pendulumetfalcatis.item.CoreTier;
import de.gilfort.pendulumetfalcatis.item.TarotCardItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Resolves which card effects are currently in play for a tool or a player. */
public final class ToolPassives {
    /** Card slot of the active card; slots after it are passive. */
    public static final int ACTIVE_CARD = 0;
    public static final int FIRST_PASSIVE_CARD = 1;
    public static final int CARD_COUNT = FIRST_PASSIVE_CARD + CoreTier.MAX_PASSIVE_SLOTS;

    private ToolPassives() {
    }

    /** A passive effect with the combined strength of all copies of its card in one tool. */
    public record Entry(TarotCard card, PassiveEffect effect, float strength) {
    }

    private record CardSide(TarotCard card, boolean reversed) {
    }

    /** A tool a player holds in a hand where it works: the scythe in the main hand, the pendulum in either. */
    public record HeldTool(ItemStack stack, ArcaneToolItem item, InteractionHand hand) {
    }

    public static NonNullList<ItemStack> getCardStacks(ItemStack tool) {
        NonNullList<ItemStack> cards = NonNullList.withSize(CARD_COUNT, ItemStack.EMPTY);
        ArcaneToolItem.getCards(tool).copyInto(cards);
        return cards;
    }

    /** The active effect of the card in the tool's active slot, or {@code null} if it is empty or the tool is inactive. */
    public static @Nullable ActiveEffect getActiveEffect(ItemStack tool) {
        if (ArcaneToolItem.isInactive(tool) || !(tool.getItem() instanceof ArcaneToolItem toolItem)) {
            return null;
        }
        ItemStack card = getCardStacks(tool).get(ACTIVE_CARD);
        return card.getItem() instanceof TarotCardItem cardItem
                ? cardItem.card().effectsFor(toolItem.kind(), TarotCardItem.isReversed(card)).active()
                : null;
    }

    /** Passive effects of the tool's unlocked passive slots. Empty if the tool is inactive. */
    public static List<Entry> getPassives(ItemStack tool) {
        CoreTier tier = ArcaneToolItem.getCoreTier(tool);
        if (tier == null || ArcaneToolItem.isInactive(tool) || !(tool.getItem() instanceof ArcaneToolItem toolItem)) {
            return List.of();
        }
        NonNullList<ItemStack> cards = getCardStacks(tool);
        // Upright and reversed copies of a card are different effects and stack separately.
        Map<CardSide, Float> strengths = new LinkedHashMap<>();
        Map<CardSide, Float> nextCopyStrength = new LinkedHashMap<>();
        for (int i = 0; i < tier.passiveSlots(); i++) {
            ItemStack stack = cards.get(FIRST_PASSIVE_CARD + i);
            if (stack.getItem() instanceof TarotCardItem cardItem) {
                CardSide side = new CardSide(cardItem.card(), TarotCardItem.isReversed(stack));
                float copyStrength = nextCopyStrength.getOrDefault(side, 1.0F);
                strengths.merge(side, copyStrength, Float::sum);
                nextCopyStrength.put(side, copyStrength / 2);
            }
        }
        List<Entry> entries = new ArrayList<>(strengths.size());
        strengths.forEach((side, strength) -> entries.add(
                new Entry(side.card(), side.card().effectsFor(toolItem.kind(), side.reversed()).passive(), strength)));
        return entries;
    }

    /** The tools whose passives currently apply to the player. */
    public static List<HeldTool> getHeldTools(Player player) {
        List<HeldTool> tools = new ArrayList<>(2);
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof ArcaneToolItem item && item.worksInHand(hand)) {
                tools.add(new HeldTool(stack, item, hand));
            }
        }
        return tools;
    }

    /** Calls {@code action} for every passive effect currently applying to the player. */
    public static void forEachPassive(Player player, PassiveAction action) {
        for (HeldTool tool : getHeldTools(player)) {
            for (Entry entry : getPassives(tool.stack())) {
                action.accept(entry.effect(), new PassiveEffect.Context(player, tool.stack(), tool.hand(), entry.strength()));
            }
        }
    }

    @FunctionalInterface
    public interface PassiveAction {
        void accept(PassiveEffect effect, PassiveEffect.Context context);
    }
}
