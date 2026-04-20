package com.susen36.apple_enchantment.mixin;

import com.susen36.apple_enchantment.AppleEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Redirect(
            method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"
            )
    )
    private void onRedirectEat(FoodData instance, int nutrition, float saturation,
                               Item item, ItemStack stack, @Nullable LivingEntity entity) {

        int finalNutrition = nutrition;

        int bountifulLevel = stack.getEnchantmentLevel(AppleEnchantments.BOUNTIFUL.get());

        if (bountifulLevel > 0) {
            // 基础 4 点 + 每级 2 点
            finalNutrition += 4 + bountifulLevel *2;
        }

        instance.eat(finalNutrition, saturation);
    }
}