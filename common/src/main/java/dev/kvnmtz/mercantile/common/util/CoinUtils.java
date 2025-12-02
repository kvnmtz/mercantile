package dev.kvnmtz.mercantile.common.util;

import dev.kvnmtz.mercantile.common.data.CoinType;
import dev.kvnmtz.mercantile.common.data.ICoinHolder;
import dev.kvnmtz.mercantile.common.item.CoinItem;
import dev.kvnmtz.mercantile.common.item.registry.ModItems;
import dev.kvnmtz.mercantile.common.network.packet.ClientboundCoinsSyncPacket;
import dev.kvnmtz.mercantile.common.network.packet.ClientboundInsufficientFundsPacket;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class CoinUtils {

    protected static int getCoinsCommon(Player player) {
        if (player == null) {
            return 0;
        }

        return ((ICoinHolder) player).mercantile$getCoins();
    }

    public static int getCoins(ServerPlayer player) {
        return getCoinsCommon(player);
    }

    protected static void setCoinsCommon(Player player, int coins) {
        if (player == null) {
            return;
        }

        ((ICoinHolder) player).mercantile$setCoins(coins);
    }

    public static void setCoins(ServerPlayer player, int coins) {
        setCoinsCommon(player, coins);
    }

    public static void addCoins(ServerPlayer player, int coins) {
        ((ICoinHolder) player).mercantile$addCoins(coins);
        syncCoinsToClient(player, true);
    }

    public static boolean removeCoins(ServerPlayer player, int coins, boolean showTransaction) {
        if (!((ICoinHolder) player).mercantile$removeCoins(coins)) {
            return false;
        }

        syncCoinsToClient(player, showTransaction);
        return true;
    }

    public static boolean hasCoins(ServerPlayer player, int coins) {
        return ((ICoinHolder) player).mercantile$hasCoins(coins);
    }

    public static void syncCoinsToClient(ServerPlayer player, boolean showTransaction) {
        new ClientboundCoinsSyncPacket(getCoins(player), showTransaction).sendTo(player);
    }

    public static void giveCoins(CoinType coinType, ServerPlayer player, boolean shiftPressed) {
        var insufficientCoins = false;

        //noinspection LoopStatementThatDoesntLoop
        do {
            var coinItem = ModItems.getCoinItem(coinType).get();
            var costPerCoin = coinItem.getCoinType().getValue();
            var playerCoins = CoinUtils.getCoins(player);

            int coinsToGive;
            if (shiftPressed) {
                var maxAffordable = playerCoins / costPerCoin;
                coinsToGive = Math.min(64, maxAffordable);
                if (coinsToGive == 0) {
                    insufficientCoins = true;
                    break;
                }
            } else {
                coinsToGive = 1;
            }

            var coins = new ItemStack(coinItem, coinsToGive);
            if (!InventoryUtils.canAddItem(player, coins)) {
                var availableSpace = InventoryUtils.getAvailableSpace(player, coins);
                if (availableSpace == 0)
                    break;

                coinsToGive = availableSpace;
                coins.setCount(coinsToGive);
            }

            var totalCost = coinsToGive * costPerCoin;
            if (!CoinUtils.hasCoins(player, totalCost)) {
                insufficientCoins = true;
                break;
            }

            if (!CoinUtils.removeCoins(player, totalCost, false))
                break;

            player.getInventory().add(coins);
            SoundUtils.playSoundForPlayer(player, SoundEvents.ITEM_PICKUP, 0.5F, 1.0F);
            return;
        } while (false);

        if (insufficientCoins) {
            new ClientboundInsufficientFundsPacket().sendTo(player);
        }
        SoundUtils.playSoundForPlayer(player, SoundEvents.NOTE_BLOCK_BASS.value(), 0.5F, 0.5F);
    }

    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

    public static MutableComponent formatAmount(int amount) {
        var formattedAmount = FORMATTER.format(amount);
        return LangUtils.translateAndKeepFormatting("text.mercantile.currency_format", formattedAmount);
    }

    public static int getCoinStackValue(ItemStack stack, int count) {
        if (!(stack.getItem() instanceof CoinItem coinItem)) {
            return 0;
        }

        return coinItem.getCoinType().getValue() * count;
    }

    public static int getCoinStackValue(ItemStack stack) {
        return getCoinStackValue(stack, stack.getCount());
    }

    public static List<ItemStack> getCoinStacksFromValue(int value) {
        var result = new ArrayList<ItemStack>();

        if (value <= 0) {
            return result;
        }

        for (var type : CoinType.getAllSorted()) {
            var coinValue = type.getValue();
            if (coinValue <= 0) {
                continue;
            }

            var amount = value / coinValue;

            if (amount > 0) {
                result.add(new ItemStack(ModItems.getCoinItem(type).get(), amount));
                value %= coinValue;
            }

            if (value == 0) {
                break;
            }
        }

        if (value != 0) {
            throw new IllegalArgumentException("Unable to weigh up value in coin items (no coin with value=1?).");
        }

        return result;
    }

    public static void storeAllCoins(ServerPlayer player) {
        var totalValue = 0;

        for (var item : player.getInventory().items) {
            if (item.isEmpty()) {
                continue;
            }

            if (!(item.getItem() instanceof CoinItem coinItem)) {
                continue;
            }

            totalValue += coinItem.getCoinType().getValue() * item.getCount();
            item.shrink(item.getCount());
        }

        if (totalValue == 0) {
            return;
        }

        addCoins(player, totalValue);
        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);
    }

    public static int getCoinsInInventory(Player player) {
        var totalValue = 0;

        for (var item : player.getInventory().items) {
            if (item.isEmpty()) {
                continue;
            }

            if (!(item.getItem() instanceof CoinItem coinItem)) {
                continue;
            }

            totalValue += coinItem.getCoinType().getValue() * item.getCount();
        }

        return totalValue;
    }
}