package dev.kvnmtz.mercantile.common.mixin;

import dev.kvnmtz.mercantile.common.data.ICoinHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerDataMixin implements ICoinHolder {

    @Unique
    private static final String COINS_TAG = "Coins";

    @Unique
    private int mercantile$storedCoins = 0;

    @Override
    public int mercantile$getCoins() {
        return mercantile$storedCoins;
    }

    @Override
    public void mercantile$setCoins(int coins) {
        mercantile$storedCoins = Math.max(0, coins);
    }

    @Override
    public void mercantile$addCoins(int coins) {
        mercantile$storedCoins += Math.max(0, coins);
    }

    @Override
    public boolean mercantile$removeCoins(int coins) {
        if (coins < 0 || mercantile$storedCoins < coins) {
            return false;
        }

        mercantile$storedCoins -= coins;
        return true;
    }

    @Override
    public boolean mercantile$hasCoins(int coins) {
        return mercantile$storedCoins >= coins;
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void saveNBTData(CompoundTag compound, CallbackInfo ci) {
        compound.putInt(COINS_TAG, mercantile$storedCoins);
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void loadNBTData(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains(COINS_TAG)) {
            mercantile$storedCoins = compound.getInt(COINS_TAG);
        }
    }
}
