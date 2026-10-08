package de.gilfort.pendulumetfalcatis.registry;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.item.CoreTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PendulumEtFalcatis.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.pendulumetfalcatis"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.SCYTHE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.SCYTHE.get());
                output.accept(ModItems.PENDULUM.get());
                for (CoreTier tier : CoreTier.values()) {
                    output.accept(ModItems.core(tier));
                }
                ModCards.all().forEach(card -> output.accept(card.get()));
            }).build());

    private ModCreativeTabs() {
    }
}
