package com.susen36.apple_enchantment.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class BaneOfEdenEnchantment extends Enchantment {

    public BaneOfEdenEnchantment() {
        super(Rarity.RARE, AppleEnchantments.ENCHANTED_APPLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 8;
    }

    @Override
    public int getMaxCost(int level) {
        return 15;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean checkCompatibility(Enchantment other) {
        //不能是增幅，也不能是丰食
        return super.checkCompatibility(other) && !(other instanceof AmplificationEnchantment) && !(other instanceof BountifulEnchantment);
    }
}