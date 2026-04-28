package com.susen36.apple_enchantment.effects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnchantAbsorptionData extends MobEffectInstance.FactorData {
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    public EnchantAbsorptionData(int paddingDuration) {
        super(paddingDuration);
    }

    public void addEnchantment(Enchantment enchantment) {
        enchantments.put(enchantment, 1);
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
    }

    public void addEnchantments(Collection<Enchantment> enchantments) {
        for (Enchantment ench : enchantments) {
            this.enchantments.put(ench, 1);
        }
    }

    public void addEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public static EnchantAbsorptionData create() {
        return new EnchantAbsorptionData(0);
    }
}
