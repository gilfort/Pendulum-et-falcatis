package de.gilfort.pendulumetfalcatis.item;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * The core installed in a scythe or pendulum. Higher tiers unlock more passive card slots
 * and improve the tool's base stats.
 */
public enum CoreTier implements StringRepresentable {
    BASIC("basic", 1, 0.0F, 1.0F),
    ADEPT("adept", 2, 1.0F, 2.0F),
    ARCANE("arcane", 3, 2.0F, 3.0F),
    ASCENDED("ascended", 4, 3.0F, 4.0F);

    public static final Codec<CoreTier> CODEC = StringRepresentable.fromEnum(CoreTier::values);
    public static final StreamCodec<ByteBuf, CoreTier> STREAM_CODEC = ByteBufCodecs.idMapper(i -> values()[i], CoreTier::ordinal);

    private final String name;
    private final int passiveSlots;
    private final float damageBonus;
    private final float durabilityMultiplier;

    CoreTier(String name, int passiveSlots, float damageBonus, float durabilityMultiplier) {
        this.name = name;
        this.passiveSlots = passiveSlots;
        this.damageBonus = damageBonus;
        this.durabilityMultiplier = durabilityMultiplier;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public int level() {
        return ordinal() + 1;
    }

    public int passiveSlots() {
        return passiveSlots;
    }

    /** Extra attack damage on top of the scythe's iron-sword base. */
    public float damageBonus() {
        return damageBonus;
    }

    /** Multiplier applied to the tool's base durability once cores can be swapped. */
    public float durabilityMultiplier() {
        return durabilityMultiplier;
    }

    public String translationKey() {
        return "core_tier.pendulumetfalcatis." + name;
    }
}
