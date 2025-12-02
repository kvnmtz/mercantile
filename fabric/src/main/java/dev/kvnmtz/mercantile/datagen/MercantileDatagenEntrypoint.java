package dev.kvnmtz.mercantile.datagen;

import dev.kvnmtz.mercantile.datagen.provider.MercantileItemModelProvider;
import dev.kvnmtz.mercantile.datagen.provider.MercantileItemTagProvider;
import dev.kvnmtz.mercantile.datagen.provider.MercantileLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MercantileDatagenEntrypoint implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator datagen) {
        var pack = datagen.createPack();
        
        pack.addProvider(MercantileLanguageProvider::new);
        pack.addProvider(MercantileItemModelProvider::new);
        pack.addProvider(MercantileItemTagProvider::new);
    }
}