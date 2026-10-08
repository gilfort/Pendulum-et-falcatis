package de.gilfort.pendulumetfalcatis.card;

/**
 * A tarot card definition. Each card has an active and a passive effect for each tool;
 * which one applies depends on the tool and slot the card is in.
 */
public record TarotCard(String name, Effects scythe, Effects pendulum) {
    public Effects effectsFor(ToolKind kind) {
        return kind == ToolKind.SCYTHE ? scythe : pendulum;
    }

    /** Translation key prefix for the card's effect descriptions, e.g. {@code tarot_card.pendulumetfalcatis.fool}. */
    public String translationKey() {
        return "tarot_card.pendulumetfalcatis." + name;
    }

    public record Effects(ActiveEffect active, PassiveEffect passive) {
    }
}
