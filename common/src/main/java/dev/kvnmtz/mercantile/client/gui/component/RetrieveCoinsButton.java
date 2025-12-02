package dev.kvnmtz.mercantile.client.gui.component;

import dev.kvnmtz.mercantile.MercantileMod;
import dev.kvnmtz.mercantile.common.data.CoinType;
import dev.kvnmtz.mercantile.common.config.ModConfig;
import dev.kvnmtz.mercantile.common.mixin.AbstractContainerScreenAccessor;
import dev.kvnmtz.mercantile.common.network.packet.ServerboundCoinCollectPacket;
import dev.kvnmtz.mercantile.common.util.CoinUtils;
import dev.kvnmtz.mercantile.common.util.LangUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class RetrieveCoinsButton extends ImageButton {

    private final AbstractContainerScreen<?> parentGui;
    private final int xOffset, yOffset;

    private final ResourceLocation coinIcon;

    public RetrieveCoinsButton(AbstractContainerScreen<?> parentGui, int xOffset, int yOffset, CoinType coinType) {
        super(((AbstractContainerScreenAccessor) parentGui).mercantile$getLeftPos() + xOffset,
                ((AbstractContainerScreenAccessor) parentGui).mercantile$getTopPos() + yOffset,
                ModConfig.RETRIEVE_COIN_BUTTON_WIDTH, ModConfig.RETRIEVE_COIN_BUTTON_HEIGHT, 0, 0,
                ModConfig.RETRIEVE_COIN_BUTTON_HEIGHT,
                MercantileMod.asResource("textures/gui/retrieve_coin_button.png"), ModConfig.RETRIEVE_COIN_BUTTON_WIDTH,
                ModConfig.RETRIEVE_COIN_BUTTON_HEIGHT * 2,
                button -> handleClick(coinType));

        this.parentGui = parentGui;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.coinIcon = MercantileMod.asResource("textures/item/" + coinType.getName() + "_coin_0.png");

        setTooltip(Tooltip.create(LangUtils.translateAndKeepFormatting("gui.mercantile.retrieve_button.tooltip",
                CoinUtils.formatAmount(coinType.getValue()))));
    }

    private static void handleClick(CoinType coinType) {
        var shiftPressed = Screen.hasShiftDown();
        new ServerboundCoinCollectPacket(coinType, shiftPressed).sendToServer();
    }

    @Override
    public void playDownSound(@NotNull SoundManager soundHandler) {
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var guiLeft = ((AbstractContainerScreenAccessor) parentGui).mercantile$getLeftPos();
        var guiTop = ((AbstractContainerScreenAccessor) parentGui).mercantile$getTopPos();
        this.setX(guiLeft + xOffset);
        this.setY(guiTop + yOffset);

        if (parentGui instanceof CreativeModeInventoryScreen creativeScreen) {
            var isInventoryTab = creativeScreen.isInventoryOpen();
            this.active = isInventoryTab;
            this.visible = isInventoryTab;

            if (!isInventoryTab)
                return;
        }

        this.active = true;
        this.visible = true;

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTexture(guiGraphics, this.coinIcon, this.getX() + ModConfig.RETRIEVE_COIN_BUTTON_ICON_OFFSET_X,
                this.getY() + ModConfig.RETRIEVE_COIN_BUTTON_ICON_OFFSET_Y, 0, 0, 0, this.width,
                this.height, ModConfig.COIN_TEXTURE_SIZE, ModConfig.COIN_TEXTURE_SIZE);
    }
}
