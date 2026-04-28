package com.susen36.apple_enchantment.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnergeticEnchantment extends Enchantment {

    public EnergeticEnchantment() {
        super(Rarity.RARE, AppleEnchantments.ENCHANTED_APPLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
    public boolean isTradeable() {
        return true;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}