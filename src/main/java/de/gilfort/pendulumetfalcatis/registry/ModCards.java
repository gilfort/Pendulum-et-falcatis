package de.gilfort.pendulumetfalcatis.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.gilfort.pendulumetfalcatis.card.TarotCard;
import de.gilfort.pendulumetfalcatis.item.TarotCardItem;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * All tarot cards. Each card registers an item {@code <name>_card} on {@link ModItems#ITEMS}.
 * <p>
 * Assets per card: {@code textures/item/card/<name>.png} (the card's own art, drawn under the shared
 * frame {@code textures/item/card_frame.png}), an item model and the translations for its effects.
 */
public final class ModCards {
    private static final List<DeferredItem<TarotCardItem>> CARDS = new ArrayList<>();

    // Cards are added here in milestone 4.

    private ModCards() {
    }

    /** Ensures this class is loaded, so all cards are registered before the item registry fires. */
    public static void init() {
    }

    public static List<DeferredItem<TarotCardItem>> all() {
        return Collections.unmodifiableList(CARDS);
    }

    private static DeferredItem<TarotCardItem> register(String name, TarotCard.Effects scythe, TarotCard.Effects pendulum) {
        TarotCard card = new TarotCard(name, scythe, pendulum);
        DeferredItem<TarotCardItem> item = ModItems.ITEMS.registerItem(name + "_card", props -> new TarotCardItem(card, props));
        CARDS.add(item);
        return item;
    }
}
