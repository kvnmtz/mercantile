package dev.kvnmtz.mercantile.common.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.kvnmtz.mercantile.MercantileMod;
import dev.kvnmtz.mercantile.common.network.packet.ClientboundCoinsSyncPacket;
import dev.kvnmtz.mercantile.common.network.packet.ClientboundInsufficientFundsPacket;
import dev.kvnmtz.mercantile.common.network.packet.ServerboundCoinCollectPacket;

public abstract class ModPackets {

    private static final SimpleNetworkManager NET = SimpleNetworkManager.create(MercantileMod.MOD_ID);

    public static final MessageType COIN_COLLECT_PACKET = NET.registerC2S("coin_collect", ServerboundCoinCollectPacket::new);
    public static final MessageType COINS_SYNC_PACKET = NET.registerS2C("coins_sync", ClientboundCoinsSyncPacket::new);
    public static final MessageType INSUFFICIENT_FUNDS_PACKET = NET.registerS2C("insufficient_funds", ClientboundInsufficientFundsPacket::new);

    @SuppressWarnings("EmptyMethod")
    public static void init() {
        // load class
    }
}