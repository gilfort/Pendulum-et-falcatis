package de.gilfort.pendulumetfalcatis.registry;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.menu.TarotTableMenu;
import de.gilfort.pendulumetfalcatis.menu.ToolMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, PendulumEtFalcatis.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ToolMenu>> TOOL = MENUS.register("tool", () -> IMenuTypeExtension.create(ToolMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TarotTableMenu>> TAROT_TABLE = MENUS.register("tarot_table",
            () -> IMenuTypeExtension.create((containerId, inventory, buf) -> new TarotTableMenu(containerId, inventory)));

    private ModMenus() {
    }
}
