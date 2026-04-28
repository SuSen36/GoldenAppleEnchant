package com.susen36.apple_enchantment.mixin;

import com.susen36.apple_enchantment.enchantment.AppleEnchantments;
import com.susen36.apple_enchantment.effects.AppleMobEffects;
import com.susen36.apple_enchantment.effects.EnchantAbsorptionEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"), cancellable = true)
    private static void onGetEnchantmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (enchantment.category != AppleEnchantments.APPLE && enchantment.category != AppleEnchantments.ENCHANTED_APPLE) {
            int originalLevel = cir.getReturnValue();
            MobEffectInstance effectInstance = entity.getEffect(AppleMobEffects.ENCHANT_ABSORPTION.get());

            if (effectInstance != null) {
                Optional<?> factorData = effectInstance.getFactorData();
                if (factorData.isPresent() && factorData.get() instanceof EnchantAbsorptionEffect.EnchantAbsorptionData data) {
                    Map<Enchantment, Integer> enchantmentMap = data.getEnchantments();
                    if (enchantmentMap.containsKey(enchantment)) {
                        int customLevel = enchantmentMap.get(enchantment);
                        cir.setReturnValue(Math.max(originalLevel, customLevel));
                    }
                }
            }
        }
    }
}
