package dev.kvnmtz.mercantile.client.gui;

import com.mojang.datafixers.util.Pair;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.platform.Platform;
import dev.kvnmtz.mercantile.client.gui.component.RetrieveCoinsButton;
import dev.kvnmtz.mercantile.client.gui.component.InventoryCoinsDisplay;
import dev.kvnmtz.mercantile.common.data.CoinType;
import dev.kvnmtz.mercantile.common.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

@Environment(EnvType.CLIENT)
public class MercantileInventoryAdditions {

    private static final boolean CURIOS_LOADED = Platform.isModLoaded("curios");
    private static Class<?> CURIOS_SCREEN_CLASS = null;

    static {
        if (CURIOS_LOADED) {
            try {
                CURIOS_SCREEN_CLASS = Class.forName("top.theillusivec4.curios.api.client.ICuriosScreen");
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
    }

    public static void register(Screen screen, ScreenAccess access) {
        var isCreative = screen instanceof CreativeModeInventoryScreen;
        var isCuriosScreen = isCuriosScreen(screen);

        if (!(screen instanceof InventoryScreen || isCreative || isCuriosScreen))
            return;

        var gui = (AbstractContainerScreen<?>) screen;

        for (var coinType : CoinType.getAllSorted().descendingSet()) {
            Pair<Integer, Integer> offsets;

            if (isCreative) {
                offsets = ModConfig.RETRIEVE_COIN_BUTTON_CREATIVE_OFFSETS.get(coinType.getName());
            } else {
                offsets = ModConfig.RETRIEVE_COIN_BUTTON_OFFSETS.get(coinType.getName());
            }

            var button = new RetrieveCoinsButton(gui, offsets.getFirst(), offsets.getSecond(), coinType);
            access.addRenderableWidget(button);
        }

        var xOffset = 0;
        int yOffset;
        if (!isCreative) {
            yOffset = -26;
        } else {
            if (Platform.isFabric()) {
                yOffset = -52;
            } else {
                yOffset = -76;
            }
        }

        access.addRenderableOnly(new InventoryCoinsDisplay(gui, xOffset, yOffset));
    }

    private static boolean isCuriosScreen(Screen screen) {
        if (!CURIOS_LOADED || CURIOS_SCREEN_CLASS == null)
            return false;

        try {
            return CURIOS_SCREEN_CLASS.isInstance(screen);
        } catch (Exception e) {
            return false;
        }
    }
}
