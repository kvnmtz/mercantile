package dev.kvnmtz.mercantile.client.event;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.kvnmtz.mercantile.client.gui.MercantileInventoryAdditions;
import dev.kvnmtz.mercantile.client.gui.component.TransactionDisplay;
import dev.kvnmtz.mercantile.common.data.ICoinHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class ModEventsClient {

    public static void init() {
        ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register((oldPlayer, newPlayer) -> {
            var oldHolder = (ICoinHolder) oldPlayer;
            var newHolder = (ICoinHolder) newPlayer;
            newHolder.mercantile$setCoins(oldHolder.mercantile$getCoins());
        });

        ClientGuiEvent.RENDER_HUD.register((graphics, tickDelta) -> TransactionDisplay.getInstance().renderHud(graphics));

        ClientGuiEvent.INIT_POST.register(MercantileInventoryAdditions::register);
    }
}
