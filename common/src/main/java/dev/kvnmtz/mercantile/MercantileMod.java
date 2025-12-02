package dev.kvnmtz.mercantile;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.kvnmtz.mercantile.client.MercantileModClient;
import dev.kvnmtz.mercantile.common.config.ModConfig;
import dev.kvnmtz.mercantile.common.event.ModEvents;
import dev.kvnmtz.mercantile.common.item.registry.ModCreativeTabs;
import dev.kvnmtz.mercantile.common.item.registry.ModItems;
import dev.kvnmtz.mercantile.common.network.ModPackets;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MercantileMod {

    public static final String MOD_ID = "mercantile";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        ModConfig.load();
        ModItems.init();
        ModCreativeTabs.init();
        ModPackets.init();
        ModEvents.init();

        if (Platform.getEnvironment() == Env.CLIENT) {
            MercantileModClient.init();
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
