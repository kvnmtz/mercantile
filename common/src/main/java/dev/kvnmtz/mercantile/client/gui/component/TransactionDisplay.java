package dev.kvnmtz.mercantile.client.gui.component;

import dev.kvnmtz.mercantile.MercantileMod;
import dev.kvnmtz.mercantile.client.util.CoinUtilsClient;
import dev.kvnmtz.mercantile.client.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class TransactionDisplay {

    private static final ResourceLocation TEXTURE = MercantileMod.asResource("textures/gui/coins_display.png");

    private static final long SLIDE_IN_DURATION_MS = 300;
    private static final long TX_SLIDE_IN_DURATION_MS = 200;
    private static final long COUNT_UP_DURATION_MS = 750;
    private static final long STAY_DURATION_MS = 750;
    private static final long WAIT_FOR_NEXT_DURATION_MS = 300;
    private static final long SLIDE_OUT_DURATION_MS = 500;
    private static final long TX_SLIDE_OUT_DURATION_MS = 200;

    private static final int WIDGET_HEIGHT = 25;
    private static final int TRANSACTION_HEIGHT = Minecraft.getInstance().font.lineHeight;
    private static final int PADDING = 4;

    private boolean isActive = false;
    private AnimationPhase currentPhase = AnimationPhase.IDLE;
    private long phaseStartTime = 0;

    private float topDisplayY = -WIDGET_HEIGHT;
    private int startingCoinAmount = 0;
    private int displayedCoinAmount = 0;

    private final List<Transaction> transactions = new ArrayList<>();

    private static TransactionDisplay INSTANCE;

    public static TransactionDisplay getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TransactionDisplay();
        }
        return INSTANCE;
    }

    private TransactionDisplay() {
    }

    public void addTransaction(int amount) {
        if (amount == 0) {
            return;
        }

        transactions.add(new Transaction(amount));

        if (!isActive) {
            startAnimation();
        } else {
            if (currentPhase == AnimationPhase.STAY) {
                currentPhase = AnimationPhase.COUNT_UP;
                phaseStartTime = System.currentTimeMillis();
                startingCoinAmount = CoinUtilsClient.getCoins();
                displayedCoinAmount = startingCoinAmount;
            } else if (currentPhase == AnimationPhase.TOP_SLIDE_OUT) {
                currentPhase = AnimationPhase.TOP_SLIDE_IN;
                startingCoinAmount = CoinUtilsClient.getCoins();
                displayedCoinAmount = startingCoinAmount;
            }
        }
    }

    public void renderHud(GuiGraphics graphics) {
        if (!isActive) return;

        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) {
            return;
        }

        var screenWidth = mc.getWindow().getGuiScaledWidth();

        updateAnimation(screenWidth);
        renderTopDisplay(graphics, screenWidth);
        renderTransactions(graphics);
    }

    private void startAnimation() {
        if (transactions.isEmpty()) return;

        isActive = true;
        currentPhase = AnimationPhase.TOP_SLIDE_IN;
        phaseStartTime = System.currentTimeMillis();
        startingCoinAmount = CoinUtilsClient.getCoins();
        displayedCoinAmount = startingCoinAmount;
    }

    private void endAnimation() {
        isActive = false;
        currentPhase = AnimationPhase.IDLE;
        topDisplayY = -WIDGET_HEIGHT;
    }

    private void updateAnimation(int screenWidth) {
        animateTransactions(screenWidth);

        var currentTime = System.currentTimeMillis();
        var elapsed = currentTime - phaseStartTime;

        switch (currentPhase) {
            case TOP_SLIDE_IN: {
                var progress = Math.min(1.0f, (float) elapsed / SLIDE_IN_DURATION_MS);
                progress = easeOutQuart(progress);
                topDisplayY = Mth.lerp(progress, -WIDGET_HEIGHT, PADDING);

                if (progress == 1.0f) {
                    currentPhase = AnimationPhase.COUNT_UP;
                    phaseStartTime = currentTime;
                }

                break;
            }

            case COUNT_UP: {
                var progress = Math.min(1.0f, (float) elapsed / COUNT_UP_DURATION_MS);
                progress = easeOutCubic(progress);

                var currentTransaction = transactions.get(0);
                var target = startingCoinAmount + currentTransaction.amount;
                displayedCoinAmount = (int) Mth.lerp(progress, startingCoinAmount, target);

                if (progress == 1.0f) {
                    currentPhase = AnimationPhase.WAIT_FOR_NEXT_TRANSACTION;
                    transactions.get(0).setPhase(Transaction.AnimationPhase.SLIDE_OUT);
                    phaseStartTime = currentTime;
                }

                break;
            }

            case WAIT_FOR_NEXT_TRANSACTION: {
                if (elapsed >= WAIT_FOR_NEXT_DURATION_MS) {
                    finishCurrentTransaction();
                    currentPhase = transactions.isEmpty() ? AnimationPhase.STAY : AnimationPhase.COUNT_UP;
                    phaseStartTime = currentTime;
                }

                break;
            }

            case STAY: {
                if (elapsed >= STAY_DURATION_MS) {
                    currentPhase = AnimationPhase.TOP_SLIDE_OUT;
                    phaseStartTime = currentTime;
                }

                break;
            }

            case TOP_SLIDE_OUT: {
                var progress = Math.min(1.0f, (float) elapsed / SLIDE_OUT_DURATION_MS);
                progress = easeInQuart(progress);
                topDisplayY = Mth.lerp(progress, PADDING, -WIDGET_HEIGHT);

                if (progress == 1.0f) {
                    endAnimation();
                }

                break;
            }
        }
    }

    private void finishCurrentTransaction() {
        if (transactions.isEmpty()) {
            return;
        }

        var completed = transactions.remove(0);

        startingCoinAmount += completed.amount;
        displayedCoinAmount = startingCoinAmount;

        if (!transactions.isEmpty()) {
            for (var i = 0; i < transactions.size(); i++) {
                transactions.get(i).y = PADDING + WIDGET_HEIGHT + PADDING + (i * TRANSACTION_HEIGHT);
            }
        }
    }

    private void renderTopDisplay(GuiGraphics graphics, int screenWidth) {
        var coinText = CoinUtilsClient.formatAmount(displayedCoinAmount).withStyle(ChatFormatting.GREEN);
        var textWidth = Minecraft.getInstance().font.width(coinText);

        final var leftWidth = 25;
        final var rightWidth = 5;

        var totalWidth = leftWidth + textWidth + rightWidth;

        var x = screenWidth - totalWidth - PADDING;
        var y = Math.round(topDisplayY);

        graphics.blit(
                TEXTURE,
                x, y,
                leftWidth, WIDGET_HEIGHT,
                0, 0,
                leftWidth, WIDGET_HEIGHT,
                leftWidth + 1 + rightWidth, WIDGET_HEIGHT
        );

        RenderUtils.blitRepeating(
                graphics,
                TEXTURE,
                x + leftWidth, y,
                textWidth, WIDGET_HEIGHT,
                leftWidth, 0,
                1, WIDGET_HEIGHT,
                leftWidth + 1 + rightWidth, WIDGET_HEIGHT
        );

        graphics.blit(
                TEXTURE,
                x + leftWidth + textWidth, y,
                rightWidth, WIDGET_HEIGHT,
                leftWidth + 1, 0,
                rightWidth, WIDGET_HEIGHT,
                leftWidth + 1 + rightWidth, WIDGET_HEIGHT
        );

        graphics.drawString(
                Minecraft.getInstance().font,
                coinText,
                x + leftWidth,
                y + 8,
                0,
                true
        );
    }

    private void animateTransactions(int screenWidth) {
        for (var i = 0; i < transactions.size(); i++) {
            var transaction = transactions.get(i);

            if (!transaction.isInitialized()) {
                var isPositive = transaction.amount > 0;
                var sign = isPositive ? "+ " : "- ";
                var displayText = Component.literal(sign).append(CoinUtilsClient.formatAmount(Mth.abs(transaction.amount)))
                        .withStyle(isPositive ? ChatFormatting.GREEN : ChatFormatting.RED);
                var textWidth = Minecraft.getInstance().font.width(displayText);

                final int widgetRightWidth = 5;
                transaction.initialize(screenWidth + textWidth, screenWidth - textWidth - PADDING - widgetRightWidth,
                        PADDING + WIDGET_HEIGHT + PADDING + (i * TRANSACTION_HEIGHT), displayText);
            }

            switch (transaction.getPhase()) {
                case SLIDE_IN: {
                    var progress = Math.min(1.0f, (float) transaction.getPhaseElapsed() / TX_SLIDE_IN_DURATION_MS);
                    progress = easeOutQuart(progress);
                    transaction.currentX = Mth.lerp(progress, transaction.startX, transaction.targetX);

                    if (progress == 1.0f) {
                        transaction.setPhase(Transaction.AnimationPhase.IDLE);
                    }

                    break;
                }

                case IDLE: {
                    break;
                }

                case SLIDE_OUT: {
                    var progress = Math.min(1.0f, (float) transaction.getPhaseElapsed() / TX_SLIDE_OUT_DURATION_MS);
                    progress = easeInQuart(progress);

                    transaction.currentX = Mth.lerp(progress, transaction.targetX, transaction.startX);
                    break;
                }
            }
        }
    }

    private void renderTransactions(GuiGraphics guiGraphics) {
        for (var transaction : transactions) {
            if (!transaction.initialized) {
                continue;
            }

            var x = (int) transaction.currentX;
            var y = (int) transaction.y;

            int color = 0xFFFFFF;
            if (transaction.getPhase() == Transaction.AnimationPhase.SLIDE_OUT) {
                var progress = Math.min(1.0f, (float) transaction.getPhaseElapsed() / TX_SLIDE_OUT_DURATION_MS);
                var opacity = Math.round(Mth.lerp(progress, 255.0f, 1.0f));
                color = FastColor.ARGB32.color(opacity, 255, 255, 255);
            }

            guiGraphics.drawString(Minecraft.getInstance().font, transaction.displayText, x, y, color, true);
        }
    }

    private float easeOutQuart(float t) {
        return 1 - (float) Math.pow(1 - t, 4);
    }

    private float easeInQuart(float t) {
        return t * t * t * t;
    }

    private float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private enum AnimationPhase {
        IDLE,
        TOP_SLIDE_IN,
        COUNT_UP,
        WAIT_FOR_NEXT_TRANSACTION,
        STAY,
        TOP_SLIDE_OUT,
    }

    private static class Transaction {
        private boolean initialized = false;
        private AnimationPhase phase;
        private long phaseStartMillis;

        final int amount;
        float startX;
        float currentX;
        float targetX;
        float y;
        Component displayText;

        Transaction(int amount) {
            this.amount = amount;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public void initialize(float startX, float targetX, float y, Component displayText) {
            initialized = true;
            this.startX = this.currentX = startX;
            this.targetX = targetX;
            this.y = y;
            this.displayText = displayText;
            setPhase(AnimationPhase.SLIDE_IN);
        }

        public AnimationPhase getPhase() {
            return phase;
        }

        public long getPhaseElapsed() {
            return System.currentTimeMillis() - phaseStartMillis;
        }

        public void setPhase(AnimationPhase phase) {
            this.phase = phase;
            this.phaseStartMillis = System.currentTimeMillis();
        }

        private enum AnimationPhase {
            SLIDE_IN,
            IDLE,
            SLIDE_OUT,
        }
    }
}
