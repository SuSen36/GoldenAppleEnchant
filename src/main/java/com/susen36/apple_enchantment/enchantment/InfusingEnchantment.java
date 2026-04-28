package com.susen36.apple_enchantment.enchantment;

import com.susen36.apple_enchantment.Config;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class InfusingEnchantment extends Enchantment {

    public InfusingEnchantment() {
        super(Rarity.COMMON, AppleEnchantments.APPLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 10;
    }

    @Override
    public int getMaxCost(int level) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isTreasureOnly() {
        return Config.INFUSING_TREASURE_ONLY.get();
    }
}