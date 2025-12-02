package dev.kvnmtz.mercantile.common.network.packet;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.kvnmtz.mercantile.common.data.CoinType;
import dev.kvnmtz.mercantile.common.network.ModPackets;
import dev.kvnmtz.mercantile.common.util.CoinUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundCoinCollectPacket extends BaseC2SMessage {

    private final CoinType coinType;
    private final boolean shiftPressed;

    public ServerboundCoinCollectPacket(CoinType coinType, boolean shiftPressed) {
        this.coinType = coinType;
        this.shiftPressed = shiftPressed;
    }

    public ServerboundCoinCollectPacket(FriendlyByteBuf buf) {
        this(CoinType.getByIndex(buf.readByte()), buf.readBoolean());
    }

    @Override
    public MessageType getType() {
        return ModPackets.COIN_COLLECT_PACKET;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeByte(this.coinType.getIndex());
        buf.writeBoolean(this.shiftPressed);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            var player = (ServerPlayer) context.getPlayer();
            if (player != null) {
                CoinUtils.giveCoins(coinType, player, shiftPressed);
            }
        });
    }
}