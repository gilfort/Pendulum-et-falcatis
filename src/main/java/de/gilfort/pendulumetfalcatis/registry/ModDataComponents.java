package de.gilfort.pendulumetfalcatis.registry;

import java.util.function.Supplier;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.item.CoreTier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PendulumEtFalcatis.MODID);

    /** The core installed in a scythe or pendulum. */
    public static final Supplier<DataComponentType<CoreTier>> CORE_TIER = COMPONENTS.registerComponentType("core_tier",
            builder -> builder.persistent(CoreTier.CODEC).networkSynchronized(CoreTier.STREAM_CODEC));

    private ModDataComponents() {
    }
}
