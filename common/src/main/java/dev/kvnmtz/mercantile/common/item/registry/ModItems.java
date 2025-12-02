package dev.kvnmtz.mercantile.common.item.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.kvnmtz.mercantile.MercantileMod;
import dev.kvnmtz.mercantile.common.data.CoinType;
import dev.kvnmtz.mercantile.common.item.CoinItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MercantileMod.MOD_ID, Registries.ITEM);

    private static final Map<CoinType, RegistrySupplier<CoinItem>> COINTYPE_TO_REGISTRYOBJECT = new HashMap<>();

    private static void registerCoinItem(CoinType coinType) {
        var registrySupplier = ITEMS.register(coinType.getName() + "_coin", () -> new CoinItem(coinType));
        COINTYPE_TO_REGISTRYOBJECT.put(coinType, registrySupplier);
    }

    public static RegistrySupplier<CoinItem> getCoinItem(CoinType coinType) {
        return COINTYPE_TO_REGISTRYOBJECT.get(coinType);
    }

    public static void init() {
        for (var coinType : CoinType.getAllSorted()) {
            registerCoinItem(coinType);
        }

        ITEMS.register();
    }
}