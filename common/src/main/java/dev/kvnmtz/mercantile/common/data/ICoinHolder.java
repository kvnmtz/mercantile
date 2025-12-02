package dev.kvnmtz.mercantile.common.data;

public interface ICoinHolder {

    int mercantile$getCoins();
    void mercantile$setCoins(int coins);
    void mercantile$addCoins(int coins);
    boolean mercantile$removeCoins(int coins);
    boolean mercantile$hasCoins(int coins);
}
