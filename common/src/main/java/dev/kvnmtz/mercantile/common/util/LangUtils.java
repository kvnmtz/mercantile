package dev.kvnmtz.mercantile.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class LangUtils {

    public static MutableComponent translateAndKeepFormatting(String key, Object... args) {
        return Component.literal(Component.translatable(key, args).getString());
    }
}
