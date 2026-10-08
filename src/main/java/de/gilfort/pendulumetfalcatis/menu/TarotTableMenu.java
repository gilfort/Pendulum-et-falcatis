package de.gilfort.pendulumetfalcatis.menu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.gilfort.pendulumetfalcatis.card.TarotCard;
import de.gilfort.pendulumetfalcatis.item.TarotCardItem;
import de.gilfort.pendulumetfalcatis.registry.ModBlocks;
import de.gilfort.pendulumetfalcatis.registry.ModCards;
import de.gilfort.pendulumetfalcatis.registry.ModMenus;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * The tarot table menu.
 * <ul>
 * <li><b>Shuffle:</b> three cards become one random card that is none of the three.</li>
 * <li><b>Flip:</b> turns a card upside down (or back) for one amethyst shard.</li>
 * </ul>
 * Actions are triggered by buttons on the client, which send a {@code TableActionPayload}.
 * The table keeps no items: whatever is left in it is returned to the player when the menu closes.
 */
public class TarotTableMenu extends AbstractContainerMenu {
    public static final int ACTION_SHUFFLE = 0;
    public static final int ACTION_FLIP = 1;

    public static final int SHUFFLE_INPUT_COUNT = 3;
    public static final int SHUFFLE_OUTPUT = 3;
    public static final int FLIP_CARD = 4;
    public static final int FLIP_FUEL = 5;
    private static final int TABLE_SLOT_COUNT = 6;
    private static final int PLAYER_SLOTS_START = TABLE_SLOT_COUNT;
    private static final int PLAYER_SLOTS_END = PLAYER_SLOTS_START + 36;

    public static final int SHUFFLE_ROW_Y = 20;
    public static final int FLIP_ROW_Y = 54;
    public static final int FIRST_INPUT_X = 26;
    public static final int OUTPUT_X = 134;
    public static final int BUTTON_X = 84;

    private static final Item FLIP_COST = Items.AMETHYST_SHARD;

    private final ContainerLevelAccess access;
    private final SimpleContainer table = new SimpleContainer(TABLE_SLOT_COUNT);

    public TarotTableMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public TarotTableMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.TAROT_TABLE.get(), containerId);
        this.access = access;

        for (int i = 0; i < SHUFFLE_INPUT_COUNT; i++) {
            addSlot(new CardSlot(table, i, FIRST_INPUT_X + i * 18, SHUFFLE_ROW_Y));
        }
        addSlot(new Slot(table, SHUFFLE_OUTPUT, OUTPUT_X, SHUFFLE_ROW_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new CardSlot(table, FLIP_CARD, FIRST_INPUT_X, FLIP_ROW_Y));
        addSlot(new Slot(table, FLIP_FUEL, FIRST_INPUT_X + 18, FLIP_ROW_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(FLIP_COST);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    /** Runs a button action on the server. */
    public void handleAction(int action, Player player) {
        boolean done = switch (action) {
            case ACTION_SHUFFLE -> shuffle(player);
            case ACTION_FLIP -> flip();
            default -> false;
        };
        if (done) {
            access.execute((level, pos) -> level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 1.0F));
            broadcastChanges();
        }
    }

    private boolean shuffle(Player player) {
        if (!table.getItem(SHUFFLE_OUTPUT).isEmpty()) {
            return false;
        }
        Set<TarotCard> inputs = new HashSet<>();
        for (int i = 0; i < SHUFFLE_INPUT_COUNT; i++) {
            if (!(table.getItem(i).getItem() instanceof TarotCardItem card)) {
                return false;
            }
            inputs.add(card.card());
        }
        List<Item> candidates = ModCards.all().stream()
                .map(card -> (Item) card.get())
                .filter(item -> !inputs.contains(((TarotCardItem) item).card()))
                .collect(Collectors.toList());
        if (candidates.isEmpty()) {
            candidates = ModCards.all().stream().map(card -> (Item) card.get()).collect(Collectors.toList());
        }
        Item result = candidates.get(player.getRandom().nextInt(candidates.size()));
        for (int i = 0; i < SHUFFLE_INPUT_COUNT; i++) {
            table.removeItem(i, 1);
        }
        table.setItem(SHUFFLE_OUTPUT, new ItemStack(result));
        return true;
    }

    private boolean flip() {
        ItemStack card = table.getItem(FLIP_CARD);
        if (!(card.getItem() instanceof TarotCardItem) || !table.getItem(FLIP_FUEL).is(FLIP_COST)) {
            return false;
        }
        TarotCardItem.setReversed(card, !TarotCardItem.isReversed(card));
        table.removeItem(FLIP_FUEL, 1);
        table.setChanged();
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (slotIndex < PLAYER_SLOTS_START) {
            if (!moveItemStackTo(stack, PLAYER_SLOTS_START, PLAYER_SLOTS_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.is(FLIP_COST)) {
            if (!moveItemStackTo(stack, FLIP_FUEL, FLIP_FUEL + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof TarotCardItem) {
            if (!moveItemStackTo(stack, 0, SHUFFLE_INPUT_COUNT, false) && !moveItemStackTo(stack, FLIP_CARD, FLIP_CARD + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
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
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, pos) -> clearContainer(player, table));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.TAROT_TABLE.get());
    }

    /** A slot that holds a single tarot card. */
    private static class CardSlot extends Slot {
        CardSlot(SimpleContainer container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof TarotCardItem;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
