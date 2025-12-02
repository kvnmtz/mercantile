package dev.kvnmtz.mercantile.client.gui.component;

import dev.kvnmtz.mercantile.MercantileMod;
import dev.kvnmtz.mercantile.client.util.CoinUtilsClient;
import dev.kvnmtz.mercantile.common.mixin.AbstractContainerScreenAccessor;
import dev.kvnmtz.mercantile.client.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class InventoryCoinsDisplay implements Renderable {

    private static final ResourceLocation TEXTURE = MercantileMod.asResource("textures/gui/coins_display.png");

    private final AbstractContainerScreen<?> parentGui;
    private final int xOffset, yOffset;

    public InventoryCoinsDisplay(AbstractContainerScreen<?> parentGui, int xOffset, int yOffset) {
        this.parentGui = parentGui;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    private static final long INSUFFICIENT_FUNDS_ANIMATION_DURATION_MS = 1000;
    private static long insufficientFundsAnimationStartTime = 0;

    public static void triggerInsufficientFundsAnimation() {
        insufficientFundsAnimationStartTime = System.currentTimeMillis();
    }

    private boolean isPlayingInsufficientFundsAnimation() {
        return System.currentTimeMillis() - insufficientFundsAnimationStartTime < INSUFFICIENT_FUNDS_ANIMATION_DURATION_MS;
    }

    private int getInsufficientFundsAnimationShakeOffsetX() {
        var elapsed = System.currentTimeMillis() - insufficientFundsAnimationStartTime;
        var shakeDuration = INSUFFICIENT_FUNDS_ANIMATION_DURATION_MS * (2.0F / 3.0F);

        if (elapsed >= shakeDuration)
            return 0;

        var progress = elapsed / shakeDuration;

        var shakesPerSecond = 20.0F;
        var startAmplitude = 3.0F;
        var amplitude = startAmplitude * (1.0F - progress);

        return (int) (Mth.sin(elapsed * shakesPerSecond * 2 * Mth.PI / 1000.0F) * amplitude);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        if (parentGui instanceof CreativeModeInventoryScreen creativeScreen) {
            var isInventoryTab = creativeScreen.isInventoryOpen();
            if (!isInventoryTab)
                return;
        }

        var coinText = CoinUtilsClient.formatAmount(CoinUtilsClient.getCoins())
                .withStyle(isPlayingInsufficientFundsAnimation() ? ChatFormatting.RED : ChatFormatting.GREEN);
        var textWidth = Minecraft.getInstance().font.width(coinText);

        var guiLeft = ((AbstractContainerScreenAccessor) parentGui).mercantile$getLeftPos();
        var baseX = guiLeft + xOffset;
        if (isPlayingInsufficientFundsAnimation()) {
            baseX += getInsufficientFundsAnimationShakeOffsetX();
        }

        var guiTop = ((AbstractContainerScreenAccessor) parentGui).mercantile$getTopPos();
        var baseY = guiTop + yOffset;

        final int leftWidth = 25;
        final int rightWidth = 5;
        final int height = 25;

        guiGraphics.blit(
                TEXTURE,
                baseX, baseY,
                leftWidth, height,
                0, 0,
                leftWidth, height,
                leftWidth + 1 + rightWidth, height
        );

        RenderUtils.blitRepeating(
                guiGraphics,
                TEXTURE,
                baseX + leftWidth, baseY,
                textWidth, height,
                leftWidth, 0,
                1, height,
                leftWidth + 1 + rightWidth, height
        );

        guiGraphics.blit(
                TEXTURE,
                baseX + leftWidth + textWidth, baseY,
                rightWidth, height,
                leftWidth + 1, 0,
                rightWidth, height,
                leftWidth + 1 + rightWidth, height
        );

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                coinText,
                baseX + leftWidth,
                baseY + 8,
                0,
                true
        );
    }
}
