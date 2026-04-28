package com.susen36.apple_enchantment;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedGoldenAppleItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AppleEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AppleEnchantment.MODID);

    public static final RegistryObject<Enchantment> INFUSING = ENCHANTMENTS.register("infusing", InfusingEnchantment::new);

    public static final RegistryObject<Enchantment> AMPLIFICATION = ENCHANTMENTS.register("amplification", AmplificationEnchantment::new);

    public static final RegistryObject<Enchantment> ENERGETIC = ENCHANTMENTS.register("energetic", EnergeticEnchantment::new);

    public static final RegistryObject<Enchantment> GLINT = ENCHANTMENTS.register("glint", GlintEnchantment::new);

    public static final RegistryObject<Enchantment> BOUNTIFUL = ENCHANTMENTS.register("bountiful", BountifulEnchantment::new);

    public static final RegistryObject<Enchantment> ENCHANT_ABSORPTION = ENCHANTMENTS.register("enchant_absorption", EnchantAbsorptionEnchantment::new);

    public static final RegistryObject<Enchantment> BANE_OF_EDEN = ENCHANTMENTS.register("bane_of_eden", BaneOfEdenEnchantment::new);

    public static EnchantmentCategory APPLE;
    public static EnchantmentCategory ENCHANTED_APPLE;

    public static void handleEnchantmentTypes() {
        APPLE = EnchantmentCategory.create("apple", (item) -> item == Items.GOLDEN_APPLE);
        ENCHANTED_APPLE = EnchantmentCategory.create("enchanted_apple", EnchantedGoldenAppleItem.class::isInstance);

        List<EnchantmentCategory> list = new ArrayList<>(Stream.of(CreativeModeTab.TAB_FOOD.getEnchantmentCategories()).toList());
        list.add(APPLE);
        list.add(ENCHANTED_APPLE);
        CreativeModeTab.TAB_FOOD.setEnchantmentCategories(list.toArray(new EnchantmentCategory[]{}));
    }
}