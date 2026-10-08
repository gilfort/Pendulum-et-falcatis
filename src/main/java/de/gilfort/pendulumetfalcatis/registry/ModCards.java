package de.gilfort.pendulumetfalcatis.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.gilfort.pendulumetfalcatis.card.MajorArcana;
import de.gilfort.pendulumetfalcatis.card.TarotCard;
import de.gilfort.pendulumetfalcatis.item.TarotCardItem;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * All tarot cards. Each card registers an item {@code <name>_card} on {@link ModItems#ITEMS}.
 * <p>
 * Assets per card: {@code textures/item/card/<name>.png} and {@code <name>_reversed.png} (the card's own
 * art, upright and upside down, drawn under the shared frame {@code textures/item/card_frame.png}),
 * item models for both sides and the translations for its effects.
 */
public final class ModCards {
    private static final List<DeferredItem<TarotCardItem>> CARDS = new ArrayList<>();

    public static final DeferredItem<TarotCardItem> MAGICIAN = register("magician", MajorArcana.magician(), MajorArcana.magicianReversed());
    public static final DeferredItem<TarotCardItem> EMPEROR = register("emperor", MajorArcana.emperor(), MajorArcana.emperorReversed());
    public static final DeferredItem<TarotCardItem> DEATH = register("death", MajorArcana.death(), MajorArcana.deathReversed());
    public static final DeferredItem<TarotCardItem> TOWER = register("tower", MajorArcana.tower(), MajorArcana.towerReversed());
    public static final DeferredItem<TarotCardItem> STAR = register("star", MajorArcana.star(), MajorArcana.starReversed());

    private ModCards() {
    }

    /** Ensures this class is loaded, so all cards are registered before the item registry fires. */
    public static void init() {
    }

    public static List<DeferredItem<TarotCardItem>> all() {
        return Collections.unmodifiableList(CARDS);
    }

    private static DeferredItem<TarotCardItem> register(String name, TarotCard.Side upright, TarotCard.Side reversed) {
        TarotCard card = new TarotCard(name, upright, reversed);
        DeferredItem<TarotCardItem> item = ModItems.ITEMS.registerItem(name + "_card", props -> new TarotCardItem(card, props));
        CARDS.add(item);
        return item;
    }
}
