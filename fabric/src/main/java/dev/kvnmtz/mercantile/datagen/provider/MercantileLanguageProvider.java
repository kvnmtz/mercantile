package dev.kvnmtz.mercantile.datagen.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class MercantileLanguageProvider extends FabricLanguageProvider {

    public MercantileLanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add("itemGroup.mercantile.coins_tab", "Coins");

        translationBuilder.add("text.mercantile.coin_desc", "§7Value: §a%s\n§7Right-click with it to store it in your purse");
        translationBuilder.add("text.mercantile.currency_format", "%s¢");

        translationBuilder.add("gui.mercantile.retrieve_button.tooltip", "§7Retrieve §a%s\n§7Left-click: §ax1§7\nShift + Left-click: §ax64");

        addCoinTranslations(translationBuilder);
    }
    
    private void addCoinTranslations(TranslationBuilder translationBuilder) {
        addCoinTranslation(translationBuilder, "copper", "Copper Coin");
        addCoinTranslation(translationBuilder, "silver", "Silver Coin"); 
        addCoinTranslation(translationBuilder, "gold", "Gold Coin");
    }
    
    private void addCoinTranslation(TranslationBuilder translationBuilder, String coinName, String displayName) {
        var itemKey = "item.mercantile." + coinName + "_coin";
        translationBuilder.add(itemKey, displayName);
    }
}