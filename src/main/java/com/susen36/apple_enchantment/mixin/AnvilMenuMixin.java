package com.susen36.apple_enchantment.mixin;

import com.susen36.apple_enchantment.AppleEnchantments;
import com.susen36.apple_enchantment.Config;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Shadow
    @Final
    private DataSlot cost;

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I")
    )
    private int apple_enchantment$allowEnchantAbsorptionStacking(Enchantment enchantment) {
        if (enchantment == AppleEnchantments.ENCHANT_ABSORPTION.get()) {
            return Config.ENCHANT_ABSORPTION_MAX_LEVEL.get();
        }
        return enchantment.getMaxLevel();
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 1)
    )
    private int apple_enchantment$bypassCountCostPenalty(ItemStack instance) {
        if (instance.is(Items.GOLDEN_APPLE) || instance.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            return 1;
        }
        return instance.getCount();
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 1)
    )
    private boolean apple_enchantment$allowAppleCombine(ItemStack instance, net.minecraft.world.item.Item item) {
        if ((instance.is(Items.GOLDEN_APPLE) || instance.is(Items.ENCHANTED_GOLDEN_APPLE))
                && (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE)) {
            return true;
        }
        return instance.is(item);
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamageableItem()Z")
    )
    private boolean apple_enchantment$appleNotDamageable(ItemStack instance) {
        if (instance.is(Items.GOLDEN_APPLE) || instance.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            return false;
        }
        return instance.isDamageableItem();
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void apple_enchantment$applyBatchEnchantCost(CallbackInfo ci) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        if (self.getSlot(0).getItem().isEmpty() || self.getSlot(1).getItem().isEmpty()) {
            return;
        }
        ItemStack left = self.getSlot(0).getItem();
        ItemStack right = self.getSlot(1).getItem();
        if (!(left.is(Items.ENCHANTED_GOLDEN_APPLE) || left.is(Items.GOLDEN_APPLE))) {
            return;
        }
        Map<Enchantment, Integer> rightEnchants = EnchantmentHelper.getEnchantments(right);
        boolean hasEnchantAbsorption = rightEnchants.containsKey(AppleEnchantments.ENCHANT_ABSORPTION.get());
        if (!hasEnchantAbsorption) {
            return;
        }
        int stackCount = left.getCount();
        if (stackCount <= 1) {
            return;
        }
        int maxBatch = Config.ENCHANT_ABSORPTION_MAX_LEVEL.get();
        // 超过配置上限时使用原版算法（显示过于昂贵）
        //if (stackCount > maxBatch) {
        //    return;
        //}
        // 经验线性递减：第1个100%，第2个90%，...，最低50%
        // 总成本 = 单个成本 × sum(每项系数)
        int singleCost = this.cost.get();
        double totalCost = 0;
        for (int i = 0; i < stackCount; i++) {
            double factor = Math.max(0.5, 1.0 - i * 0.1);
            totalCost += singleCost * factor;
        }
        this.cost.set((int) Math.ceil(totalCost));
        if (this.cost.get() >= 40) {
            this.cost.set(39);
        }
    }
}