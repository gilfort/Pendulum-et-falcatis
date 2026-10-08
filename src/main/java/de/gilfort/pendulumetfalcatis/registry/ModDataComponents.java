package de.gilfort.pendulumetfalcatis.registry;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.item.CoreTier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PendulumEtFalcatis.MODID);

    /** The core installed in a scythe or pendulum. */
    public static final Supplier<DataComponentType<CoreTier>> CORE_TIER = COMPONENTS.registerComponentType("core_tier",
            builder -> builder.persistent(CoreTier.CODEC).networkSynchronized(CoreTier.STREAM_CODEC));

    /** The tarot cards in a scythe or pendulum: slot 0 is the active slot, slots 1–4 are passive. */
    public static final Supplier<DataComponentType<ItemContainerContents>> CARDS = COMPONENTS.registerComponentType("cards",
            builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));

    /** Present on a tarot card that has been turned upside down at the tarot table; absent means upright. */
    public static final Supplier<DataComponentType<Boolean>> REVERSED = COMPONENTS.registerComponentType("reversed",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    private ModDataComponents() {
    }
}
