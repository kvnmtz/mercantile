package dev.kvnmtz.mercantile.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.kvnmtz.mercantile.MercantileMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MercantileMod.MOD_ID)
public final class MercantileModForge {

    @SuppressWarnings("removal")
    public MercantileModForge() {
        EventBuses.registerModEventBus(MercantileMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        MercantileMod.init();
    }
}
