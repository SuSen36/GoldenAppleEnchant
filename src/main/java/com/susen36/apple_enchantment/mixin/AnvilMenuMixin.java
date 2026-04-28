package com.susen36.apple_enchantment.mixin;

import com.susen36.apple_enchantment.enchantment.AppleEnchantments;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Shadow
    @Final
    private DataSlot cost;

    @Unique
    private boolean hasAbsorptionEnchant() {
        ItemStack right = this.getSelf().getSlot(1).getItem();
        Map<Enchantment, Integer> rightEnchants = EnchantmentHelper.getEnchantments(right);
        return rightEnchants.containsKey(AppleEnchantments.ENCHANT_ABSORPTION.get());
    }

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
            if (!hasAbsorptionEnchant()) {
                return instance.getCount();
            }
            return Math.max(1, instance.getCount() / 5);
        }
        return instance.getCount();
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamageableItem()Z")
    )
    private boolean apple_enchantment$appleNotDamageable(ItemStack instance) {
        if (instance.is(Items.GOLDEN_APPLE) || instance.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            if (hasAbsorptionEnchant()) {
                return false;
            }
        }
        return instance.isDamageableItem();
    }

    @ModifyVariable(
            method = "createResult",
            at = @At(value = "STORE", ordinal = 0),
            argsOnly = false,
            index = 2
    )
    private int apple_enchantment$blockPenalty40(int value) {
        if (value == 40) {
            ItemStack left = this.getSelf().getSlot(0).getItem();
            if (left.is(Items.GOLDEN_APPLE) || left.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                if (!hasAbsorptionEnchant()) {
                    return value;
                }
                int maxLevel = Config.ENCHANT_ABSORPTION_MAX_LEVEL.get();
                if (left.getCount() <= maxLevel) {
                    return 0;
                }
            }
        }
        return value;
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void apple_enchantment$applyBatchEnchantCost(CallbackInfo ci) {
        if (this.getSelf().getSlot(0).getItem().isEmpty() || this.getSelf().getSlot(1).getItem().isEmpty()) {
            return;
        }
        ItemStack left = this.getSelf().getSlot(0).getItem();
        ItemStack right = this.getSelf().getSlot(1).getItem();
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
        // 每个金苹果单独计算成本后相加，递减系数：第1个100%，第2个90%，...，最低50%
        // 原版单次附魔成本除以8作为基数，使批量附魔的总成本大幅低于逐次操作
        // 例：原版cost=7 → baseCost=0.875，3个金苹果总成本≈3（而非逐次操作的7+9+11=27）
        double baseCost = this.cost.get() / 8.0;
        double totalCost = 0;
        for (int i = 0; i < stackCount; i++) {
            double factor = Math.max(0.5, 1.0 - i * 0.1);
            totalCost += baseCost * factor;
        }
        this.cost.set((int) Math.ceil(totalCost));
        if (this.cost.get() >= 40) {
            this.cost.set(39);
        }
    }

    private AnvilMenu getSelf() {
        return (AnvilMenu) (Object) this;
    }
}