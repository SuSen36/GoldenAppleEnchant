package com.susen36.apple_enchantment.effects;

import com.susen36.apple_enchantment.AppleEnchantment;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AppleMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AppleEnchantment.MODID);

    public static final RegistryObject<MobEffect> ENCHANT_ABSORPTION = MOB_EFFECTS.register("enchant_absorption", EnchantAbsorptionEffect::new);
}
