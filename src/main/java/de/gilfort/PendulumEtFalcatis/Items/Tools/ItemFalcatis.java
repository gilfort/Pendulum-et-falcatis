package de.gilfort.PendulumEtFalcatis.Items.Tools;

import de.gilfort.PendulumEtFalcatis.Main_PendulumEtFalcatis;
import net.minecraft.item.Item;

public class ItemFalcatis extends net.minecraft.item.ItemSword{

	private String name;
	
	public ItemFalcatis(ToolMaterial material, String name) {
		super(material);
		setRegistryName(name);
		setUnlocalizedName(name);
		this.name = name;
		setCreativeTab(Main_PendulumEtFalcatis.creativeTab);
	}
	
	public void registerItemModel(Item item) {
		Main_PendulumEtFalcatis.proxy.registerItemRenderer(this, 0, name);
	}
	
}
