package com.susen36.apple_enchantment.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Collection;
import java.util.Optional;

public class EnchantAbsorptionEffect extends MobEffect {

    public EnchantAbsorptionEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8932B4);
        this.setFactorDataFactory(() -> new EnchantAbsorptionData(0));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;

    }

    public static MobEffectInstance createWithEnchantments(MobEffectInstance base, Collection<Enchantment> enchantments) {
        EnchantAbsorptionData data = EnchantAbsorptionData.create();
        data.addEnchantments(enchantments);
        return new MobEffectInstance(
                AppleMobEffects.ENCHANT_ABSORPTION.get(),
                base.getDuration(),
                base.getAmplifier(),
                base.isAmbient(),
                base.isVisible(),
                base.showIcon(),
                base.hiddenEffect,
                Optional.of(data)
        );
    }

    public static MobEffectInstance createWithMobEffect(MobEffectInstance effect, Collection<Enchantment> enchantments) {
        return createWithEnchantments(effect, enchantments);
    }

    public static MobEffectInstance createWithMobEffect(MobEffectInstance effect, Enchantment enchantment) {
        return createWithMobEffect(effect, java.util.List.of(enchantment));
    }
}