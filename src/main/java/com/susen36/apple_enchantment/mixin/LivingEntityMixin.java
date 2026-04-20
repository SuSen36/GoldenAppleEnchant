package com.susen36.apple_enchantment.mixin;

import com.mojang.datafixers.util.Pair;
import com.susen36.apple_enchantment.AppleEnchantment;
import com.susen36.apple_enchantment.AppleEnchantments;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(
            method = "addEatEffect",
            at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;")
    )
    private Iterator<Pair<MobEffectInstance, Float>> onGetFoodEffectsIterator(List<Pair<MobEffectInstance, Float>> originalList, ItemStack stack, Level level, LivingEntity entity) {
        ArrayList<Pair<MobEffectInstance, Float>> newEffectsList = new ArrayList<>(originalList);

        int energeticLevel = stack.getEnchantmentLevel(AppleEnchantments.ENERGETIC.get());
        if (energeticLevel > 0) {
            int duration = 6000; // 5分钟
            newEffectsList.add(Pair.of(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 1), 1.0F));
            newEffectsList.add(Pair.of(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 0), 1.0F));
            newEffectsList.add(Pair.of(new MobEffectInstance(MobEffects.DIG_SPEED, duration, 0), 1.0F));
        }

        int glintLevel = stack.getEnchantmentLevel(AppleEnchantments.GLINT.get());
        if (glintLevel > 0) {

            int nvDuration = (int) (7200 * (1.0 + 0.25 * glintLevel));
            newEffectsList.add(Pair.of(new MobEffectInstance(MobEffects.NIGHT_VISION, nvDuration, 0), 1.0F));// 夜视:基础6分钟,每级 +25% 持续时间
            newEffectsList.add(Pair.of(new MobEffectInstance(MobEffects.GLOWING, 400, glintLevel - 1), 1.0F));//发光:固定20秒,每级附魔提升1级效果
        }

        int baneOfEdenLevel = stack.getEnchantmentLevel(AppleEnchantments.BANE_OF_EDEN.get());
        if (baneOfEdenLevel > 0) {
            newEffectsList.add(Pair.of(new MobEffectInstance(MobEffects.HUNGER, 400, 1), 1.0F)); // 饥饿 II: 20秒
            newEffectsList.add(Pair.of(new MobEffectInstance(MobEffects.CONFUSION, 300, 0), 1.0F)); // 反胃: 15秒
        }

        if (entity instanceof ServerPlayer player) {
            // 附魔不能有诅咒(成就
            if (!EnchantmentHelper.getEnchantments(stack).isEmpty() && EnchantmentHelper.getEnchantments(stack).keySet().stream().noneMatch(Enchantment::isCurse)) {
                ResourceLocation advId = new ResourceLocation(AppleEnchantment.MODID, "overpowered");
                Advancement advancement = player.getServer().getAdvancements().getAdvancement(advId);

                if (advancement != null) {
                    player.getAdvancements().award(advancement, "requirement");
                }
            }
        }
        return newEffectsList.iterator();
    }

    @Redirect(
            method = "addEatEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z")
    )
    private boolean onAddEatEffect(LivingEntity entity, MobEffectInstance effect, ItemStack stack) {
        if (stack.is(Items.ENCHANTED_GOLDEN_APPLE) && stack.getEnchantmentLevel(AppleEnchantments.AMPLIFICATION.get()) > 0) {
            int ampLevel = stack.getEnchantmentLevel(AppleEnchantments.AMPLIFICATION.get());

            int newDuration = (int) (effect.getDuration() * (1.0 + 0.2 * ampLevel));// 持续时间：每级+20%
            int newAmplifier = (int) Math.min(255, effect.getAmplifier() + ampLevel * 0.5F);// 强度：每两级+1，上限255

            MobEffectInstance enhancedEffect = new MobEffectInstance(
                    effect.getEffect(),
                    newDuration,
                    newAmplifier,
                    effect.isAmbient(),
                    effect.isVisible()
            );

            return entity.addEffect(enhancedEffect);
        }
        return entity.addEffect(effect);
    }
}