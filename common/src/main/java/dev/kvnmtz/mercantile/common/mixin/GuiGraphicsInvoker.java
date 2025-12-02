package dev.kvnmtz.mercantile.common.mixin;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsInvoker {

    @Invoker("slices")
    static IntIterator invokeSlices(int target, int total) {
        throw new AssertionError();
    }
}