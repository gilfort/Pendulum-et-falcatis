package de.gilfort.pendulumetfalcatis;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.gilfort.pendulumetfalcatis.event.ToolEvents;
import de.gilfort.pendulumetfalcatis.network.AbilityHandler;
import de.gilfort.pendulumetfalcatis.network.UseAbilityPayload;
import de.gilfort.pendulumetfalcatis.registry.ModCards;
import de.gilfort.pendulumetfalcatis.registry.ModCreativeTabs;
import de.gilfort.pendulumetfalcatis.registry.ModDataComponents;
import de.gilfort.pendulumetfalcatis.registry.ModItems;
import de.gilfort.pendulumetfalcatis.registry.ModMenus;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(PendulumEtFalcatis.MODID)
public class PendulumEtFalcatis {
    public static final String MODID = "pendulumetfalcatis";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PendulumEtFalcatis(IEventBus modEventBus) {
        ModDataComponents.COMPONENTS.register(modEventBus);
        ModCards.init();
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);

        modEventBus.addListener(PendulumEtFalcatis::registerPayloads);

        NeoForge.EVENT_BUS.register(ToolEvents.class);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(UseAbilityPayload.TYPE, UseAbilityPayload.STREAM_CODEC, AbilityHandler::handle);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
