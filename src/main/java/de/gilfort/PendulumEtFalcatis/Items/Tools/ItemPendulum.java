package de.gilfort.PendulumEtFalcatis.Items.Tools;

import de.gilfort.PendulumEtFalcatis.Main_PendulumEtFalcatis;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

public class ItemPendulum extends net.minecraft.item.ItemShield{

private String name;
	
	public ItemPendulum(String name) {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		this.name = name;
		setCreativeTab(Main_PendulumEtFalcatis.creativeTab);
	}
	
	public void registerItemModel(Item item) {
		Main_PendulumEtFalcatis.proxy.registerItemRenderer(this, 0, name);
	}
	
}
