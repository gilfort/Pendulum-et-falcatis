package de.gilfort.PendulumEtFalcatis.Items;

import de.gilfort.PendulumEtFalcatis.Main_PendulumEtFalcatis;
import de.gilfort.PendulumEtFalcatis.Items.Tools.ItemFalcatis;
import de.gilfort.PendulumEtFalcatis.Items.Tools.ItemPendulum;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

	//items
	public static ItemBase gemDull = new ItemBase("gemDull").setCreativeTab(Main_PendulumEtFalcatis.creativeTab);
	
	//tools
	public static ItemFalcatis toolIronScythe = new ItemFalcatis(ToolMaterial.IRON, "ironScythe");
	public static ItemPendulum toolPendulum = new ItemPendulum("ironPendulum");
	
	public static void register(IForgeRegistry<Item> registry) {
		registry.registerAll(
				gemDull,
				toolIronScythe,
				toolPendulum
				);
	}
	
	public static void registerModels() {
		gemDull.registerItemModel();
		toolIronScythe.registerItemModel(toolIronScythe);
		toolPendulum.registerItemModel(toolPendulum);
	}
	
}
