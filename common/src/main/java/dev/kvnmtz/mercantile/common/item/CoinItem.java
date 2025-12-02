package dev.kvnmtz.mercantile.common.item;

import dev.architectury.platform.Platform;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.utils.Env;
import dev.kvnmtz.mercantile.common.data.CoinType;
import dev.kvnmtz.mercantile.common.util.CoinUtils;
import dev.kvnmtz.mercantile.common.util.LangUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public class CoinItem extends Item {
    
    private final CoinType coinType;

    public CoinItem(CoinType coinType) {
        super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.UNCOMMON));
        this.coinType = coinType;

        if (Platform.getEnvironment() == Env.CLIENT) {
            registerCountItemProperty();
        }
    }

    private void registerCountItemProperty() {
        ItemPropertiesRegistry.register(
                this,
                new ResourceLocation("count"),
                (stack, level, entity, seed) -> {
                    var isItemEntity = entity == null;
                    if (isItemEntity) {
                        return 0.0f;
                    }

                    //noinspection DataFlowIssue
                    if (stack.hasTag() && stack.getTag().getBoolean("CreativeTabIcon")) {
                        return 1.0f;
                    }

                    return ((float) stack.getCount()) / stack.getMaxStackSize();
                }
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        if (player.isShiftKeyDown() && !level.isClientSide()) {
            CoinUtils.storeAllCoins((ServerPlayer) player);
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }

        var stackInHand = player.getItemInHand(hand);
        var totalValue = CoinUtils.getCoinStackValue(stackInHand);
        
        if (!level.isClientSide()) {
            CoinUtils.addCoins((ServerPlayer) player, totalValue);
        } else {
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);
        }

        player.setItemInHand(hand, ItemStack.EMPTY);

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        var component = LangUtils.translateAndKeepFormatting("text.mercantile.coin_desc", CoinUtils.formatAmount(coinType.getValue()));
        var lines = component.getString().split("\n");

        for (var line : lines) {
            tooltip.add(Component.literal(line));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }

    public CoinType getCoinType() {
        return coinType;
    }
}