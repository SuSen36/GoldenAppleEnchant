package com.susen36.apple_enchantment;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AppleEnchantment.MODID)
public class AppleEnchantment
{
    public static final String MODID = "apple_enchantment";

    public AppleEnchantment(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        AppleEnchantments.initEnchantmentCategories();

        AppleEnchantments.ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
