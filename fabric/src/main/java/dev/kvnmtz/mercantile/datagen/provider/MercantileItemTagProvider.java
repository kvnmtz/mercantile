package dev.kvnmtz.mercantile.datagen.provider;

import dev.kvnmtz.mercantile.MercantileMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public class MercantileItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public static final TagKey<Item> COIN_TAG = TagKey.create(Registries.ITEM, MercantileMod.asResource("coin"));

    public MercantileItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        var coinTagBuilder = getOrCreateTagBuilder(COIN_TAG);

        coinTagBuilder.add(MercantileMod.asResource("copper_coin"));
        coinTagBuilder.add(MercantileMod.asResource("silver_coin"));
        coinTagBuilder.add(MercantileMod.asResource("gold_coin"));
    }
}