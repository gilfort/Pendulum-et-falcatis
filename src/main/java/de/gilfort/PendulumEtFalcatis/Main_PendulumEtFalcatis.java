package de.gilfort.PendulumEtFalcatis;

import de.gilfort.PendulumEtFalcatis.Items.ModItems;
import de.gilfort.PendulumEtFalcatis.proxy.CommonProxy;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



@Mod(modid = References.MODID, name = References.NAME, version = References.VERSION)
public class Main_PendulumEtFalcatis
{

	public static FalcatisTab creativeTab = new FalcatisTab();
	
	@SidedProxy(serverSide = "de.gilfort.PendulumEtFalcatis.proxy.CommonProxy", clientSide = "de.gilfort.PendulumEtFalcatis.proxy.ClientProxy")
	public static CommonProxy proxy;

	@Mod.Instance(References.MODID)
	public static Main_PendulumEtFalcatis instance;
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler {
		
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
			ModItems.register(event.getRegistry());
		}
		
		@SubscribeEvent
		public static void registerItems(ModelRegistryEvent event) {
			ModItems.registerModels();
		}
		
	}
	
	
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
       
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
    
}
