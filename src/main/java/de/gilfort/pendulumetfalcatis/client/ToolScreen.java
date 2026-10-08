package de.gilfort.pendulumetfalcatis.client;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.item.CoreTier;
import de.gilfort.pendulumetfalcatis.menu.ToolMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** Screen for {@link ToolMenu}: core slot, active card slot and the passive card slots. */
public class ToolScreen extends AbstractContainerScreen<ToolMenu> {
    private static final Identifier TEXTURE = PendulumEtFalcatis.id("textures/gui/container/tool.png");
    /** Position of the "locked slot" overlay in the texture. */
    private static final int LOCKED_U = 176;
    private static final int LOCKED_V = 0;
    private static final int LABEL_Y = 24;
    private static final int LABEL_COLOR = 0xFF404040;

    private static final Component CORE_LABEL = Component.translatable("gui.pendulumetfalcatis.tool.core");
    private static final Component ACTIVE_LABEL = Component.translatable("gui.pendulumetfalcatis.tool.active");
    private static final Component PASSIVE_LABEL = Component.translatable("gui.pendulumetfalcatis.tool.passive");

    public ToolScreen(ToolMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Locked slots get an overlay; cards stored in them are still drawn on top so they can be taken out.
        if (!menu.isActiveSlotUnlocked()) {
            drawLocked(graphics, x + ToolMenu.ACTIVE_X, y + ToolMenu.TOOL_SLOTS_Y);
        }
        for (int i = menu.unlockedPassiveSlots(); i < CoreTier.MAX_PASSIVE_SLOTS; i++) {
            drawLocked(graphics, x + ToolMenu.FIRST_PASSIVE_X + i * 18, y + ToolMenu.TOOL_SLOTS_Y);
        }

        graphics.text(this.font, CORE_LABEL, x + ToolMenu.CORE_X - 1, y + LABEL_Y, LABEL_COLOR, false);
        graphics.text(this.font, ACTIVE_LABEL, x + ToolMenu.ACTIVE_X - 1, y + LABEL_Y, LABEL_COLOR, false);
        graphics.text(this.font, PASSIVE_LABEL, x + ToolMenu.FIRST_PASSIVE_X - 1, y + LABEL_Y, LABEL_COLOR, false);
    }

    /** Draws the locked overlay over the slot whose item is drawn at (slotX, slotY); its frame starts one pixel up and left. */
    private static void drawLocked(GuiGraphicsExtractor graphics, int slotX, int slotY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, slotX - 1, slotY - 1, LOCKED_U, LOCKED_V, 18, 18, 256, 256);
    }
}
