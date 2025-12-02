package dev.kvnmtz.mercantile.client.util;

import dev.kvnmtz.mercantile.common.util.CoinUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public abstract class CoinUtilsClient extends CoinUtils {

    public static int getCoins() {
        return getCoinsCommon(Minecraft.getInstance().player);
    }

    public static void setCoins(int coins) {
        setCoinsCommon(Minecraft.getInstance().player, coins);
    }
}
