package de.gilfort.pendulumetfalcatis;

import de.gilfort.pendulumetfalcatis.client.AbilityKeys;
import de.gilfort.pendulumetfalcatis.client.TarotTableScreen;
import de.gilfort.pendulumetfalcatis.client.ToolScreen;
import de.gilfort.pendulumetfalcatis.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = PendulumEtFalcatis.MODID, dist = Dist.CLIENT)
public class PendulumEtFalcatisClient {
    public PendulumEtFalcatisClient(IEventBus modEventBus) {
        modEventBus.addListener(PendulumEtFalcatisClient::registerScreens);
        modEventBus.addListener(AbilityKeys::register);
        NeoForge.EVENT_BUS.addListener(AbilityKeys::onClientTick);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.TOOL.get(), ToolScreen::new);
        event.register(ModMenus.TAROT_TABLE.get(), TarotTableScreen::new);
    }
}
