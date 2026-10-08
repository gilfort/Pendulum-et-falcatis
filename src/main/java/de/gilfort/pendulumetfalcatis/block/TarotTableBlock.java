package de.gilfort.pendulumetfalcatis.block;

import de.gilfort.pendulumetfalcatis.menu.TarotTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** The tarot table: shuffle three cards into a new one, or turn a card upside down. Items are returned when the menu closes. */
public class TarotTableBlock extends Block {
    private static final Component TITLE = Component.translatable("container.pendulumetfalcatis.tarot_table");

    public TarotTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, p) -> new TarotTableMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), TITLE));
        }
        return InteractionResult.SUCCESS;
    }
}
