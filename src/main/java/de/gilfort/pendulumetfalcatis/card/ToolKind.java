package de.gilfort.pendulumetfalcatis.card;

import java.util.Locale;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** The two tools a tarot card can be slotted into. Each card has separate effects for both. */
public enum ToolKind {
    SCYTHE,
    PENDULUM;

    public static final StreamCodec<ByteBuf, ToolKind> STREAM_CODEC = ByteBufCodecs.idMapper(i -> values()[i], ToolKind::ordinal);

    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
