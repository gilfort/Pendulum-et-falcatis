package de.gilfort.pendulumetfalcatis;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.gilfort.pendulumetfalcatis.event.ToolEvents;
import de.gilfort.pendulumetfalcatis.registry.ModCreativeTabs;
import de.gilfort.pendulumetfalcatis.registry.ModDataComponents;
import de.gilfort.pendulumetfalcatis.registry.ModItems;
import de.gilfort.pendulumetfalcatis.registry.ModMenus;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(PendulumEtFalcatis.MODID)
public class PendulumEtFalcatis {
    public static final String MODID = "pendulumetfalcatis";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PendulumEtFalcatis(IEventBus modEventBus) {
        ModDataComponents.COMPONENTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);

        NeoForge.EVENT_BUS.register(ToolEvents.class);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
