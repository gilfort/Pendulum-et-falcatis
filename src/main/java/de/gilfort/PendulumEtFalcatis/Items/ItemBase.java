package de.gilfort.PendulumEtFalcatis.Items;

import org.apache.logging.log4j.core.net.Facility;

import de.gilfort.PendulumEtFalcatis.Main_PendulumEtFalcatis;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item{

	protected String name;
	
	public ItemBase(String name) {
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);
	}
	
	public void registerItemModel() {
		Main_PendulumEtFalcatis.proxy.registerItemRenderer(this,0,name);
	}
	
	@Override
	public ItemBase setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
	
}
