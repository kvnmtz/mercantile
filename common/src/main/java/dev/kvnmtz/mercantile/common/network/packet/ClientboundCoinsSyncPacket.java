package dev.kvnmtz.mercantile.common.network.packet;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.kvnmtz.mercantile.client.gui.component.TransactionDisplay;
import dev.kvnmtz.mercantile.client.util.CoinUtilsClient;
import dev.kvnmtz.mercantile.common.network.ModPackets;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundCoinsSyncPacket extends BaseS2CMessage {
    
    private final int coinAmount;
    private final boolean showTransaction;

    public ClientboundCoinsSyncPacket(int coinAmount, boolean showTransaction) {
        this.coinAmount = coinAmount;
        this.showTransaction = showTransaction;
    }
    
    public ClientboundCoinsSyncPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    @Override
    public MessageType getType() {
        return ModPackets.COINS_SYNC_PACKET;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(coinAmount);
        buf.writeBoolean(showTransaction);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (showTransaction) {
                var coinsDelta = coinAmount - CoinUtilsClient.getCoins();
                TransactionDisplay.getInstance().addTransaction(coinsDelta);
            }

            CoinUtilsClient.setCoins(coinAmount);
        });
    }
}