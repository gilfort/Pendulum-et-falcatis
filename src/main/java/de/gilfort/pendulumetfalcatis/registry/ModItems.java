package de.gilfort.pendulumetfalcatis.registry;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.item.PendulumItem;
import de.gilfort.pendulumetfalcatis.item.ScytheItem;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PendulumEtFalcatis.MODID);

    public static final DeferredItem<ScytheItem> SCYTHE = ITEMS.registerItem("scythe", ScytheItem::new);

    public static final DeferredItem<PendulumItem> PENDULUM = ITEMS.registerItem("pendulum",
            props -> new PendulumItem(props.repairable(ItemTags.IRON_TOOL_MATERIALS)));

    private ModItems() {
    }
}
