package com.susen36.apple_enchantment;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = AppleEnchantment.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue INFUSING_TREASURE_ONLY = BUILDER
            .comment("Whether the Infusing enchantment should be treasure-only (cannot be obtained via enchanting table)")
            .define("infusingTreasureOnly", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean infusingTreasureOnly;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        infusingTreasureOnly = INFUSING_TREASURE_ONLY.get();
    }
}