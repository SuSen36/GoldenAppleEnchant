package com.susen36.apple_enchantment;

import com.susen36.apple_enchantment.enchantment.AppleEnchantments;
import com.susen36.apple_enchantment.effects.AppleMobEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AppleEnchantment.MODID)
public class AppleEnchantment
{
    public static final String MODID = "apple_enchantment";

    public AppleEnchantment(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        Config.register();
        AppleEnchantments.handleEnchantmentTypes();

        AppleEnchantments.ENCHANTMENTS.register(modEventBus);
        AppleMobEffects.MOB_EFFECTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }
}