package dev.kvnmtz.mercantile.common.event;

import dev.architectury.event.events.common.PlayerEvent;
import dev.kvnmtz.mercantile.common.data.ICoinHolder;
import dev.kvnmtz.mercantile.common.util.CoinUtils;

public abstract class ModEvents {

    public static void init() {
        PlayerEvent.PLAYER_CLONE.register((oldPlayer, newPlayer, wonGame) -> {
            var oldHolder = (ICoinHolder) oldPlayer;
            var newHolder = (ICoinHolder) newPlayer;
            newHolder.mercantile$setCoins(oldHolder.mercantile$getCoins());
        });

        PlayerEvent.PLAYER_JOIN.register((player) -> CoinUtils.syncCoinsToClient(player, false));
    }
}