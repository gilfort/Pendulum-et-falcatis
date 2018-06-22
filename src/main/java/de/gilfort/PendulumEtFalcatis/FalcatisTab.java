package de.gilfort.PendulumEtFalcatis;

import de.gilfort.PendulumEtFalcatis.Items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class FalcatisTab extends CreativeTabs{

	public FalcatisTab() {
		super(References.MODID);
	}
	
	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(ModItems.gemDull);
	}
	
}
