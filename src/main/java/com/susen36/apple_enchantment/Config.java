package com.susen36.apple_enchantment;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue INFUSING_TREASURE_ONLY;

    static {
        BUILDER.push("enchantments");
        INFUSING_TREASURE_ONLY = BUILDER
                .comment("Whether the Infusing enchantment should be treasure-only (default: false - obtainable via enchantment table)")
                .define("infusingTreasureOnly", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}