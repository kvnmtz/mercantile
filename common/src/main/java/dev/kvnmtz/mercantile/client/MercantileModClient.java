package dev.kvnmtz.mercantile.client;

import dev.kvnmtz.mercantile.client.event.ModEventsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class MercantileModClient {

    public static void init() {
        ModEventsClient.init();
    }
}
