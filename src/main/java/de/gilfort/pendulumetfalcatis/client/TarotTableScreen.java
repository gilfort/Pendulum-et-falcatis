package de.gilfort.pendulumetfalcatis.client;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.menu.TarotTableMenu;
import de.gilfort.pendulumetfalcatis.network.TableActionPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/** Screen for {@link TarotTableMenu}: a shuffle row and a flip row, each with a button. */
public class TarotTableScreen extends AbstractContainerScreen<TarotTableMenu> {
    private static final Identifier TEXTURE = PendulumEtFalcatis.id("textures/gui/container/tarot_table.png");
    private static final int BUTTON_WIDTH = 44;
    private static final int BUTTON_HEIGHT = 20;

    public TarotTableScreen(TarotTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addActionButton("gui.pendulumetfalcatis.tarot_table.shuffle", TarotTableMenu.SHUFFLE_ROW_Y, TarotTableMenu.ACTION_SHUFFLE);
        addActionButton("gui.pendulumetfalcatis.tarot_table.flip", TarotTableMenu.FLIP_ROW_Y, TarotTableMenu.ACTION_FLIP);
    }

    private void addActionButton(String translationKey, int slotY, int action) {
        addRenderableWidget(Button.builder(Component.translatable(translationKey),
                        button -> ClientPacketDistributor.sendToServer(new TableActionPayload(menu.containerId, action)))
                .bounds(leftPos + TarotTableMenu.BUTTON_X, topPos + slotY - 2, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
