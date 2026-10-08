package de.gilfort.pendulumetfalcatis.network;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.card.ToolKind;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client → server: the player pressed the ability key of the given tool. The server checks everything else. */
public record UseAbilityPayload(ToolKind tool) implements CustomPacketPayload {
    public static final Type<UseAbilityPayload> TYPE = new Type<>(PendulumEtFalcatis.id("use_ability"));
    public static final StreamCodec<ByteBuf, UseAbilityPayload> STREAM_CODEC = ToolKind.STREAM_CODEC.map(UseAbilityPayload::new, UseAbilityPayload::tool);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
