package com.susen36.apple_enchantment.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class BountifulEnchantment extends Enchantment {

    public BountifulEnchantment() {
        super(Rarity.COMMON, AppleEnchantments.ENCHANTED_APPLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 6;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 10;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}