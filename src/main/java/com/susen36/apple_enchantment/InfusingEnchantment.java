package com.susen36.apple_enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class InfusingEnchantment extends Enchantment {

    public InfusingEnchantment() {
        super(Rarity.COMMON, AppleEnchantments.APPLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 10;
    }

    @Override
    public int getMaxCost(int p_45173_) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
