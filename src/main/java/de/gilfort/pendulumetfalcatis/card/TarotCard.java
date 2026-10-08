package de.gilfort.pendulumetfalcatis.card;

/**
 * A tarot card definition with an upright and a reversed side. Each side has an active and a passive
 * effect per tool, plus a one-time "reading" that consumes the card when used directly.
 * Reversed effects are stronger but come with a drawback.
 */
public record TarotCard(String name, Side upright, Side reversed) {
    public Side side(boolean isReversed) {
        return isReversed ? reversed : upright;
    }

    public Effects effectsFor(ToolKind kind, boolean isReversed) {
        return side(isReversed).effectsFor(kind);
    }

    /**
     * Translation key prefix for one side's effect descriptions,
     * e.g. {@code tarot_card.pendulumetfalcatis.tower} or {@code tarot_card.pendulumetfalcatis.tower.reversed}.
     */
    public String translationKey(boolean isReversed) {
        return "tarot_card.pendulumetfalcatis." + name + (isReversed ? ".reversed" : "");
    }

    /**
     * One side of a card.
     *
     * @param reading the one-time effect when the card is used directly; the card is consumed if it fires.
     *                It is a stronger or longer version of the card's own tool effects.
     */
    public record Side(Effects scythe, Effects pendulum, ActiveEffect reading) {
        public Effects effectsFor(ToolKind kind) {
            return kind == ToolKind.SCYTHE ? scythe : pendulum;
        }
    }

    public record Effects(ActiveEffect active, PassiveEffect passive) {
    }
}
