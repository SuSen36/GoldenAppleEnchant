package com.susen36.apple_enchantment.mixin;

import com.susen36.apple_enchantment.effects.AppleMobEffects;
import com.susen36.apple_enchantment.effects.EnchantAbsorptionData;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin {

    @Inject(method = "writeDetailsTo", at = @At("TAIL"))
    private void appleEnchantment$writeEnchantments(CompoundTag tag, CallbackInfo ci) {
        MobEffectInstance self = (MobEffectInstance) (Object) this;
        if (self.getEffect() != AppleMobEffects.ENCHANT_ABSORPTION.get()) {
            return;
        }
        Optional<?> fd = self.getFactorData();
        if (fd.isPresent() && fd.get() instanceof EnchantAbsorptionData data && !data.getEnchantments().isEmpty()) {
            if (!tag.contains("FactorCalculationData", 10)) {
                return;
            }
            CompoundTag factorTag = tag.getCompound("FactorCalculationData");
            ListTag list = new ListTag();
            for (Enchantment ench : data.getEnchantments()) {
                CompoundTag entry = new CompoundTag();
                entry.putString("id", EnchantmentHelper.getEnchantmentId(ench).toString());
                list.add(entry);
            }
            factorTag.put("Enchantments", list);
        }
    }

    @ModifyVariable(method = "loadSpecifiedEffect", at = @At(value = "LOAD"), ordinal = 0)
    private static Optional<MobEffectInstance.FactorData> appleEnchantment$restoreEnchantments(
            Optional<MobEffectInstance.FactorData> optional,
            MobEffect effect,
            CompoundTag tag
    ) {
        if (effect != AppleMobEffects.ENCHANT_ABSORPTION.get()) {
            return optional;
        }
        if (!tag.contains("FactorCalculationData", 10)) {
            return optional;
        }
        CompoundTag factorTag = tag.getCompound("FactorCalculationData");
        if (!factorTag.contains("Enchantments", 9)) {
            return optional;
        }
        EnchantAbsorptionData data = EnchantAbsorptionData.create();
        ListTag list = factorTag.getList("Enchantments", 10);
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(list.getCompound(i).getString("id"));
            if (id != null) {
                Enchantment ench = Registry.ENCHANTMENT.get(id);
                if (ench != null) {
                    data.addEnchantment(ench);
                }
            }
        }
        if (data.getEnchantments().isEmpty()) {
            return optional;
        }
        return Optional.of(data);
    }

    @Redirect(
        method = "<init>(Lnet/minecraft/world/effect/MobEffectInstance;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;createFactorData()Ljava/util/Optional;")
    )
    private Optional<MobEffectInstance.FactorData> appleEnchantment$preserveFactorData(MobEffect effect, MobEffectInstance original) {
        if (effect == AppleMobEffects.ENCHANT_ABSORPTION.get()) {
            return original.getFactorData();
        }
        return effect.createFactorData();
    }
}
