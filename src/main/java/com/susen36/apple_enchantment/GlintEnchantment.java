package com.susen36.apple_enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class GlintEnchantment extends Enchantment {

    public GlintEnchantment() {
        super(Rarity.RARE, AppleEnchantments.ENCHANTED_APPLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 5 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level)  * 15;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}