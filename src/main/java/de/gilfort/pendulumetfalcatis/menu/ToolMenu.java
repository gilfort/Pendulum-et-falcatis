package de.gilfort.pendulumetfalcatis.menu;

import org.jspecify.annotations.Nullable;

import de.gilfort.pendulumetfalcatis.card.ToolPassives;
import de.gilfort.pendulumetfalcatis.item.ArcaneToolItem;
import de.gilfort.pendulumetfalcatis.item.CoreItem;
import de.gilfort.pendulumetfalcatis.item.CoreTier;
import de.gilfort.pendulumetfalcatis.item.TarotCardItem;
import de.gilfort.pendulumetfalcatis.registry.ModDataComponents;
import de.gilfort.pendulumetfalcatis.registry.ModItems;
import de.gilfort.pendulumetfalcatis.registry.ModMenus;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * The menu opened with sneak + use on a scythe or pendulum. It edits the tool held in {@link #hand}:
 * one core slot, one active card slot and up to four passive card slots.
 * <p>
 * The core can be taken out, e.g. to craft it into the next tier. Card slots the current core does
 * not unlock (all of them without a core) are locked: cards in them stay stored but have no effect,
 * and they can be taken out but not put in. The tool itself is locked in place while the menu is open.
 */
public class ToolMenu extends AbstractContainerMenu {
    public static final int CORE_SLOT = 0;
    public static final int ACTIVE_SLOT = 1;
    public static final int FIRST_PASSIVE_SLOT = 2;
    public static final int TOOL_SLOT_COUNT = FIRST_PASSIVE_SLOT + CoreTier.MAX_PASSIVE_SLOTS;
    /** Number of card slots stored in the {@link ModDataComponents#CARDS} component. */
    public static final int CARD_COUNT = ToolPassives.CARD_COUNT;

    public static final int CORE_X = 26;
    public static final int ACTIVE_X = 62;
    public static final int FIRST_PASSIVE_X = 98;
    public static final int TOOL_SLOTS_Y = 35;

    private static final int PLAYER_SLOTS_START = TOOL_SLOT_COUNT;
    private static final int PLAYER_SLOTS_END = PLAYER_SLOTS_START + 36;
    /** Button value of a {@link ContainerInput#SWAP} click that targets the offhand. */
    private static final int OFFHAND_SWAP_BUTTON = 40;

    private final Player player;
    private final InteractionHand hand;
    private final ItemStack tool;
    private final int lockedHotbarSlot;
    private final SimpleContainer toolContainer;
    private boolean loading;

    public ToolMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, buf.readEnum(InteractionHand.class));
    }

    public ToolMenu(int containerId, Inventory inventory, InteractionHand hand) {
        super(ModMenus.TOOL.get(), containerId);
        this.player = inventory.player;
        this.hand = hand;
        this.tool = player.getItemInHand(hand);
        this.lockedHotbarSlot = hand == InteractionHand.MAIN_HAND ? inventory.getSelectedSlot() : -1;
        this.toolContainer = new SimpleContainer(TOOL_SLOT_COUNT) {
            @Override
            public void setChanged() {
                super.setChanged();
                ToolMenu.this.saveToTool();
            }
        };

        loadFromTool();

        addSlot(new CoreSlot(toolContainer, CORE_SLOT, CORE_X, TOOL_SLOTS_Y));
        addSlot(new CardSlot(toolContainer, ACTIVE_SLOT, ACTIVE_X, TOOL_SLOTS_Y, -1));
        for (int i = 0; i < CoreTier.MAX_PASSIVE_SLOTS; i++) {
            addSlot(new CardSlot(toolContainer, FIRST_PASSIVE_SLOT + i, FIRST_PASSIVE_X + i * 18, TOOL_SLOTS_Y, i));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(col == lockedHotbarSlot ? new LockedSlot(inventory, col, 8 + col * 18, 142) : new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    /** The core currently in the core slot, or {@code null} if it is empty. */
    public @Nullable CoreTier installedTier() {
        return toolContainer.getItem(CORE_SLOT).getItem() instanceof CoreItem core ? core.tier() : null;
    }

    public boolean isActiveSlotUnlocked() {
        return installedTier() != null;
    }

    public int unlockedPassiveSlots() {
        CoreTier tier = installedTier();
        return tier == null ? 0 : tier.passiveSlots();
    }

    private void loadFromTool() {
        loading = true;
        CoreTier tier = ArcaneToolItem.getCoreTier(tool);
        toolContainer.setItem(CORE_SLOT, tier == null ? ItemStack.EMPTY : new ItemStack(ModItems.core(tier)));
        NonNullList<ItemStack> cards = NonNullList.withSize(CARD_COUNT, ItemStack.EMPTY);
        ArcaneToolItem.getCards(tool).copyInto(cards);
        for (int i = 0; i < CARD_COUNT; i++) {
            toolContainer.setItem(ACTIVE_SLOT + i, cards.get(i));
        }
        loading = false;
    }

    /** Writes the container back into the tool. Only the server is authoritative; the client receives the result through sync. */
    private void saveToTool() {
        if (loading || player.level().isClientSide() || !(tool.getItem() instanceof ArcaneToolItem toolItem)) {
            return;
        }
        CoreTier tier = installedTier();
        if (tier != ArcaneToolItem.getCoreTier(tool)) {
            toolItem.installCore(tool, tier);
        }
        NonNullList<ItemStack> cards = NonNullList.withSize(CARD_COUNT, ItemStack.EMPTY);
        for (int i = 0; i < CARD_COUNT; i++) {
            cards.set(i, toolContainer.getItem(ACTIVE_SLOT + i).copy());
        }
        tool.set(ModDataComponents.CARDS.get(), ItemContainerContents.fromItems(cards));
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput input, Player player) {
        // Number-key and offhand swaps could move the locked tool out of its slot.
        if (input == ContainerInput.SWAP && (button == OFFHAND_SWAP_BUTTON || button == lockedHotbarSlot)) {
            return;
        }
        super.clicked(slotId, button, input, player);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack carried, Slot slot) {
        return slot.container != toolContainer && super.canTakeItemForPickAll(carried, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem() || !slot.mayPickup(player)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (slotIndex < PLAYER_SLOTS_START) {
            if (!moveItemStackTo(stack, PLAYER_SLOTS_START, PLAYER_SLOTS_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof CoreItem) {
            if (!moveItemStackTo(stack, CORE_SLOT, CORE_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!(stack.getItem() instanceof TarotCardItem) || !moveItemStackTo(stack, ACTIVE_SLOT, TOOL_SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(hand) == tool && tool.getItem() instanceof ArcaneToolItem;
    }

    private class CoreSlot extends Slot {
        CoreSlot(SimpleContainer container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof CoreItem;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private class CardSlot extends Slot {
        /** Index among the passive slots, or -1 for the active slot. */
        private final int passiveIndex;

        CardSlot(SimpleContainer container, int index, int x, int y, int passiveIndex) {
            super(container, index, x, y);
            this.passiveIndex = passiveIndex;
        }

        public boolean isUnlocked() {
            return passiveIndex < 0 ? isActiveSlotUnlocked() : passiveIndex < unlockedPassiveSlots();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof TarotCardItem && isUnlocked();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private static class LockedSlot extends Slot {
        LockedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
