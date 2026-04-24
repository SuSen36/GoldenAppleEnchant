package com.susen36.apple_enchantment;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AppleEnchantment.MODID)
public class AppleEnchantment
{
    public static final String MODID = "apple_enchantment";

    public AppleEnchantment(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        AppleEnchantments.handleEnchantmentTypes();

        AppleEnchantments.ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }
}