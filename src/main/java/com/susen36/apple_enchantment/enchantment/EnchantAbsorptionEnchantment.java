package com.susen36.apple_enchantment.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantAbsorptionEnchantment extends Enchantment {

    public EnchantAbsorptionEnchantment() {
        super(Rarity.UNCOMMON, AppleEnchantments.ENCHANTED_APPLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 15;
    }

    @Override
    public int getMaxCost(int level) {
        return 30;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }
    
    @Override
    public int getMaxLevel() {
        return 1;
    }
}