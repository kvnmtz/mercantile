package dev.kvnmtz.mercantile.common.mixin.create;

import com.simibubi.create.content.equipment.blueprint.BlueprintOverlayRenderer;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import dev.kvnmtz.mercantile.client.util.CoinUtilsClient;
import dev.kvnmtz.mercantile.common.item.CoinItem;
import net.createmod.catnip.data.Couple;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
        value = BlueprintOverlayRenderer.class,
        remap = false
)
public abstract class BlueprintOverlayRendererMixin {

    @Invoker
    private static boolean invokeCanAfford(Player player, BigItemStack entry) {
        throw new AssertionError();
    }

    @Redirect(
            method = "displayShoppingList",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/equipment/blueprint/BlueprintOverlayRenderer;canAfford" +
                            "(Lnet/minecraft/world/entity/player/Player;" +
                            "Lcom/simibubi/create/content/logistics/BigItemStack;)Z"
            )
    )
    private static boolean mercantile$displayShoppingList(Player player, BigItemStack entry,
                                                      Couple<InventorySummary> bakedList) {

        if (!(entry.stack.getItem() instanceof CoinItem)) {
            return invokeCanAfford(player, entry);
        }

        var totalCoinValue = 0;

        for (var tallyEntry : bakedList.getSecond().getStacksByCount()) {
            if (!(tallyEntry.stack.getItem() instanceof CoinItem)) {
                continue;
            }

            totalCoinValue += CoinUtilsClient.getCoinStackValue(tallyEntry.stack, tallyEntry.count);
        }

        return totalCoinValue <= CoinUtilsClient.getCoins() + CoinUtilsClient.getCoinsInInventory(player);
    }
}
