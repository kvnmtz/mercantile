package dev.kvnmtz.mercantile.client.util;

import dev.kvnmtz.mercantile.common.mixin.GuiGraphicsInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public abstract class RenderUtils {

    public static void blitRepeating(GuiGraphics graphics, ResourceLocation atlasLocation, int x, int y, int width,
                                     int height, int uOffset, int vOffset, int sourceWidth, int sourceHeight,
                                     int textureWidth, int textureHeight) {
        var i = x;

        int j;
        for (var intIterator = GuiGraphicsInvoker.invokeSlices(width, sourceWidth); intIterator.hasNext(); i += j) {
            j = intIterator.nextInt();
            var k = (sourceWidth - j) / 2;
            var l = y;

            int m;
            for (var intIterator2 = GuiGraphicsInvoker.invokeSlices(height, sourceHeight); intIterator2.hasNext(); l += m) {
                m = intIterator2.nextInt();
                var n = (sourceHeight - m) / 2;
                graphics.blit(atlasLocation, i, l, uOffset + k, vOffset + n, j, m, textureWidth, textureHeight);
            }
        }

    }
}
