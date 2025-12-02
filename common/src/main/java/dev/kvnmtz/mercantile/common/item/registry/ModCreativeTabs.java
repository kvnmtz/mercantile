package dev.kvnmtz.mercantile.common.item.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.kvnmtz.mercantile.MercantileMod;
import dev.kvnmtz.mercantile.common.data.CoinType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public abstract class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(MercantileMod.MOD_ID, Registries.CREATIVE_MODE_TAB);

    @SuppressWarnings("unused")
    public static final RegistrySupplier<CreativeModeTab> COINS_TAB = CREATIVE_MODE_TABS.register("coins_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.mercantile.coins_tab"))
                    .icon(() -> {
                        var coinTypes = CoinType.getAllSorted();
                        if (!coinTypes.isEmpty()) {
                            var highestValueCoin = coinTypes.first();
                            var coinItem = ModItems.getCoinItem(highestValueCoin);
                            if (coinItem != null) {
                                var iconStack = new ItemStack(coinItem.get());
                                var tag = iconStack.getOrCreateTag();
                                tag.putBoolean("CreativeTabIcon", true);
                                iconStack.setTag(tag);
                                return iconStack;
                            }
                        }
                        return ItemStack.EMPTY;
                    })
                    .displayItems((parameters, output) -> {
                        for (var coinType : CoinType.getAllSorted().descendingSet()) {
                            var coinItem = ModItems.getCoinItem(coinType);
                            if (coinItem != null) {
                                output.accept(coinItem.get());
                            }
                        }
                    })
                    .build()
    );

    public static void init() {
        CREATIVE_MODE_TABS.register();
    }
}