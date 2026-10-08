package de.gilfort.pendulumetfalcatis.network;

import de.gilfort.pendulumetfalcatis.PendulumEtFalcatis;
import de.gilfort.pendulumetfalcatis.menu.TarotTableMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client → server: a button of the tarot table menu was pressed. */
public record TableActionPayload(int containerId, int action) implements CustomPacketPayload {
    public static final Type<TableActionPayload> TYPE = new Type<>(PendulumEtFalcatis.id("table_action"));
    public static final StreamCodec<ByteBuf, TableActionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TableActionPayload::containerId,
            ByteBufCodecs.VAR_INT, TableActionPayload::action,
            TableActionPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TableActionPayload payload, IPayloadContext context) {
        if (context.player().containerMenu instanceof TarotTableMenu menu && menu.containerId == payload.containerId()) {
            menu.handleAction(payload.action(), context.player());
        }
    }
}
