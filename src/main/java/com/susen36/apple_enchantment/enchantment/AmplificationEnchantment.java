package com.susen36.apple_enchantment.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class AmplificationEnchantment extends Enchantment {

    public AmplificationEnchantment() {
        super(Rarity.UNCOMMON, AppleEnchantments.ENCHANTED_APPLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 8;
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}