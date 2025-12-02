package dev.kvnmtz.mercantile.fabric;

import dev.kvnmtz.mercantile.MercantileMod;
import net.fabricmc.api.ModInitializer;

public final class MercantileModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MercantileMod.init();
    }
}
