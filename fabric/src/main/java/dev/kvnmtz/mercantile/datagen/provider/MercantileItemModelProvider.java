package dev.kvnmtz.mercantile.datagen.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.kvnmtz.mercantile.MercantileMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;

public class MercantileItemModelProvider extends FabricModelProvider {

    public MercantileItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        generateCoinModels(itemModelGenerator, "copper");
        generateCoinModels(itemModelGenerator, "silver");
        generateCoinModels(itemModelGenerator, "gold");
    }

    private void generateCoinModels(ItemModelGenerators itemModelGenerator, String coinType) {
        for (var i = 0; i <= 4; i++) {
            generateCoinStackModel(itemModelGenerator, coinType, i);
        }

        generateCoinParentModel(itemModelGenerator, coinType);
    }

    private void generateCoinStackModel(ItemModelGenerators itemModelGenerator, String coinType, int stackLevel) {
        var itemId = MercantileMod.asResource(coinType + "_coin_" + stackLevel);
        var textureLocation = MercantileMod.asResource("item/" + coinType + "_coin_" + stackLevel);

        var stackModel = new JsonObject();
        stackModel.addProperty("parent", "minecraft:item/generated");

        var textures = new JsonObject();
        textures.addProperty("layer0", textureLocation.toString());
        stackModel.add("textures", textures);

        var modelLocation = new ResourceLocation(itemId.getNamespace(), "item/" + itemId.getPath());
        itemModelGenerator.output.accept(modelLocation, () -> stackModel);
    }

    private void generateCoinParentModel(ItemModelGenerators itemModelGenerator, String coinType) {
        var itemId = MercantileMod.asResource(coinType + "_coin");

        var parentModel = new JsonObject();
        parentModel.addProperty("parent", "minecraft:item/generated");

        var textures = new JsonObject();
        textures.addProperty("layer0", "mercantile:item/" + coinType + "_coin_0");
        parentModel.add("textures", textures);

        var overrides = new JsonArray();
        
        // 1
        addOverride(overrides, 0.0, "mercantile:item/" + coinType + "_coin_0");
        // >=2
        addOverride(overrides, 0.03125, "mercantile:item/" + coinType + "_coin_1");
        // >=16
        addOverride(overrides, 0.25, "mercantile:item/" + coinType + "_coin_2");
        // >=32
        addOverride(overrides, 0.5, "mercantile:item/" + coinType + "_coin_3");
        // 64
        addOverride(overrides, 1.0, "mercantile:item/" + coinType + "_coin_4");
        
        parentModel.add("overrides", overrides);

        var modelLocation = new ResourceLocation(itemId.getNamespace(), "item/" + itemId.getPath());
        itemModelGenerator.output.accept(modelLocation, () -> parentModel);
    }

    private void addOverride(JsonArray overrides, double countPredicate, String modelPath) {
        var override = new JsonObject();

        var predicate = new JsonObject();
        predicate.addProperty("count", countPredicate);
        override.add("predicate", predicate);
        
        override.addProperty("model", modelPath);
        overrides.add(override);
    }
}