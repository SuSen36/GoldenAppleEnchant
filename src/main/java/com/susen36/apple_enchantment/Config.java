package com.susen36.apple_enchantment;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue INFUSING_TREASURE_ONLY;
    public static final ForgeConfigSpec.IntValue ENCHANT_ABSORPTION_MAX_LEVEL;

    static {
        BUILDER.push("enchantments");
        INFUSING_TREASURE_ONLY = BUILDER
                .comment("Whether the Infusing enchantment should be treasure-only (default: false - obtainable via enchantment table)")
                .define("infusingTreasureOnly", false);
        ENCHANT_ABSORPTION_MAX_LEVEL = BUILDER
                .comment("Max stack size for batch Enchant Absorption on enchanted golden apple via anvil (default: 16, max: 32)")
                .defineInRange("enchantAbsorptionMaxLevel", 16, 1, 32);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}