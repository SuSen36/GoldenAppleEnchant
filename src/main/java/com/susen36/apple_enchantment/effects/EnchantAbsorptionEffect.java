package com.susen36.apple_enchantment.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnchantAbsorptionEffect extends MobEffect {

    public EnchantAbsorptionEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8932B4);
        this.setFactorDataFactory(EnchantAbsorptionData::new);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public static MobEffectInstance createWithEnchantments(MobEffectInstance base, Map<Enchantment, Integer> enchantments) {
        EnchantAbsorptionData data = new EnchantAbsorptionData();
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

    public static class EnchantAbsorptionData extends MobEffectInstance.FactorData {
        private final Map<Enchantment, Integer> enchantments = new HashMap<>();

        public EnchantAbsorptionData() {
            super(0);
        }

        public void addEnchantment(Enchantment enchantment, int level) {
            enchantments.put(enchantment, level);
        }

        public void addEnchantments(Map<Enchantment, Integer> enchantments) {
            this.enchantments.putAll(enchantments);
        }

        public Map<Enchantment, Integer> getEnchantments() {
            return enchantments;
        }
    }
}