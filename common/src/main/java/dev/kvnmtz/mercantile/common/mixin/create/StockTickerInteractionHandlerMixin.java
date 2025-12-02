package dev.kvnmtz.mercantile.common.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.stockTicker.StockTickerInteractionHandler;
import dev.kvnmtz.mercantile.common.item.CoinItem;
import dev.kvnmtz.mercantile.common.util.CoinUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(
        value = StockTickerInteractionHandler.class,
        remap = false
)
public abstract class StockTickerInteractionHandlerMixin {

    /**
     * This injects before the inventory iteration:
     * <br>
     * {@code for (int i = 0; i < player.getInventory().items.size(); i++)}
     */
    @Inject(
            method = "interactWithShop",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Ljava/util/ArrayList;<init>()V",
                            ordinal = 0
                    )
            ),
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=0",
                    ordinal = 0
            )
    )
    private static void mercantile$interactWithShop(Player player, Level level, BlockPos targetPos, ItemStack mainHandItem,
                                                CallbackInfo ci, @Local boolean simulate,
                                                @Local(ordinal = 3) InventorySummary tally,
                                                @Local List<ItemStack> toTransfer) {

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        var totalCoinValue = 0;

        for (var tallyEntry : tally.getStacksByCount()) {
            var item = tallyEntry.stack;
            if (!(item.getItem() instanceof CoinItem)) {
                continue;
            }

            var itemCount = tallyEntry.count;

            var coinStackValue = CoinUtils.getCoinStackValue(item, itemCount);
            totalCoinValue += coinStackValue;

            if (!simulate) {
                toTransfer.add(item.copyWithCount(itemCount));
            }

            tally.add(item, -itemCount);
        }

        if (totalCoinValue == 0) {
            return;
        }

        CoinUtils.storeAllCoins(serverPlayer);

        var leftoverCoinValue = Math.max(0, totalCoinValue - CoinUtils.getCoins(serverPlayer));
        for (var leftoverCoinStack : CoinUtils.getCoinStacksFromValue(leftoverCoinValue)) {
            // as all coins have been stored (-> aren't in the inventory anymore), this will make the trade fail
            tally.add(leftoverCoinStack);
        }

        if (!simulate) {
            CoinUtils.removeCoins(serverPlayer, totalCoinValue, true);
        }
    }
}
