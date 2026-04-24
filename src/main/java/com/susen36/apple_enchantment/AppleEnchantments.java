package com.susen36.apple_enchantment;

import net.minecraft.world.item.EnchantedGoldenAppleItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AppleEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AppleEnchantment.MODID);

    public static final RegistryObject<Enchantment> INFUSING = ENCHANTMENTS.register("infusing", InfusingEnchantment::new);

    public static final RegistryObject<Enchantment> AMPLIFICATION = ENCHANTMENTS.register("amplification", AmplificationEnchantment::new);

    public static final RegistryObject<Enchantment> ENERGETIC = ENCHANTMENTS.register("energetic", EnergeticEnchantment::new);

    public static final RegistryObject<Enchantment> GLINT = ENCHANTMENTS.register("glint", GlintEnchantment::new);

    public static final RegistryObject<Enchantment> BOUNTIFUL = ENCHANTMENTS.register("bountiful", BountifulEnchantment::new);

    public static final RegistryObject<Enchantment> BANE_OF_EDEN = ENCHANTMENTS.register("bane_of_eden", BaneOfEdenEnchantment::new);

    public static EnchantmentCategory APPLE;
    public static EnchantmentCategory ENCHANTED_APPLE;

    public static void handleEnchantmentTypes() {
        APPLE = EnchantmentCategory.create("apple", (item) -> item == Items.GOLDEN_APPLE);
        ENCHANTED_APPLE = EnchantmentCategory.create("enchanted_apple", (item) -> item == Items.ENCHANTED_GOLDEN_APPLE || item instanceof EnchantedGoldenAppleItem);
    }
}