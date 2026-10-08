package de.gilfort.pendulumetfalcatis.registry;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.block.TarotTableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PendulumEtFalcatis.MODID);

    public static final DeferredBlock<TarotTableBlock> TAROT_TABLE = BLOCKS.registerBlock("tarot_table", TarotTableBlock::new,
            props -> props.mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> TAROT_TABLE_ITEM = ModItems.ITEMS.registerSimpleBlockItem("tarot_table", TAROT_TABLE);

    private ModBlocks() {
    }
}
