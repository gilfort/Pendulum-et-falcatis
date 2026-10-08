package de.gilfort.pendulumetfalcatis.registry;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
    /** Items accepted in the card slots of a scythe or pendulum. */
    public static final TagKey<Item> TAROT_CARDS = TagKey.create(Registries.ITEM, PendulumEtFalcatis.id("tarot_cards"));

    private ModTags() {
    }
}
