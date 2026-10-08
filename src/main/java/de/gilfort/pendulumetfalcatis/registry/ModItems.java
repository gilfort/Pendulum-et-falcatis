package de.gilfort.pendulumetfalcatis.registry;

import java.util.EnumMap;
import java.util.Map;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.item.CoreItem;
import de.gilfort.pendulumetfalcatis.item.CoreTier;
import de.gilfort.pendulumetfalcatis.item.PendulumItem;
import de.gilfort.pendulumetfalcatis.item.ScytheItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PendulumEtFalcatis.MODID);

    public static final DeferredItem<ScytheItem> SCYTHE = ITEMS.registerItem("scythe", ScytheItem::new);

    public static final DeferredItem<PendulumItem> PENDULUM = ITEMS.registerItem("pendulum",
            props -> new PendulumItem(props.repairable(ItemTags.IRON_TOOL_MATERIALS)));

    private static final Map<CoreTier, DeferredItem<CoreItem>> CORES = new EnumMap<>(CoreTier.class);

    static {
        for (CoreTier tier : CoreTier.values()) {
            CORES.put(tier, ITEMS.registerItem(tier.getSerializedName() + "_core", props -> new CoreItem(tier, props)));
        }
    }

    public static Item core(CoreTier tier) {
        return CORES.get(tier).get();
    }

    private ModItems() {
    }
}
