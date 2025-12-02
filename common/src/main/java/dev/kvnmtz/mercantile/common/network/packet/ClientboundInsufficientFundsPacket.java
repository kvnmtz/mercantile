package dev.kvnmtz.mercantile.common.network.packet;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.kvnmtz.mercantile.client.gui.component.InventoryCoinsDisplay;
import dev.kvnmtz.mercantile.common.network.ModPackets;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundInsufficientFundsPacket extends BaseS2CMessage {
    
    public ClientboundInsufficientFundsPacket() {
    }

    public ClientboundInsufficientFundsPacket(FriendlyByteBuf ignoredBuf) {
    }

    @Override
    public MessageType getType() {
        return ModPackets.INSUFFICIENT_FUNDS_PACKET;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(InventoryCoinsDisplay::triggerInsufficientFundsAnimation);
    }
}