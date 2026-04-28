package com.susen36.apple_enchantment.effects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EnchantAbsorptionData extends MobEffectInstance.FactorData {
    private final Set<Enchantment> enchantments = new HashSet<>();

    public EnchantAbsorptionData(int paddingDuration) {
        super(paddingDuration);
    }

    public void addEnchantment(Enchantment enchantment) {
        enchantments.add(enchantment);
    }

    public void addEnchantments(Collection<Enchantment> enchantments) {
        this.enchantments.addAll(enchantments);
    }

    public Set<Enchantment> getEnchantments() {
        return enchantments;
    }

    public static EnchantAbsorptionData create() {
        return new EnchantAbsorptionData(0);
    }
}