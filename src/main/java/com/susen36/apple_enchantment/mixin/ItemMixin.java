package com.susen36.apple_enchantment.mixin;

import com.mojang.datafixers.util.Pair;
import com.susen36.apple_enchantment.AppleEnchantment;
import com.susen36.apple_enchantment.AppleEnchantments;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = Item.class)
public abstract class ItemMixin implements IForgeItem {

    @Shadow @Final @Nullable private FoodProperties foodProperties;

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
    private void onInventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected, CallbackInfo ci) {
        if (!level.isClientSide) {
            Container container = null;

            if (entity instanceof Player player) {
                container = player.getInventory();
            } else if (entity instanceof Container entityContainer) {
                container = entityContainer;
            }

            if (container != null && stack.getItem() == Items.GOLDEN_APPLE
                    && stack.getEnchantmentLevel(AppleEnchantments.INFUSING.get()) > 0) {

                ItemStack result = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, stack.getCount());

                if (stack.hasTag() && stack.getTag() != null) {
                    result.setTag(stack.getTag().copy());
                }

                removeSpecificEnchantment(result, AppleEnchantments.INFUSING.get());
                container.setItem(slotId, result);

                if (entity instanceof ServerPlayer player) {
                    ResourceLocation advId = ResourceLocation.fromNamespaceAndPath(AppleEnchantment.MODID, "aura_of_gold");
                    Advancement advancement = player.getServer().getAdvancements().getAdvancement(advId);

                    if (advancement != null) {
                        player.getAdvancements().award(advancement, "requirement");
                    }
                }

                ci.cancel();
            }
        }
    }

    @Unique
    private static void removeSpecificEnchantment(ItemStack stack, Enchantment enchantmentToRemove) {
        Map<Enchantment, Integer> currentEnchantments = EnchantmentHelper.getEnchantments(stack);

        if (currentEnchantments.containsKey(enchantmentToRemove)) {
            Map<Enchantment, Integer> newEnchantments = new HashMap<>(currentEnchantments);
            newEnchantments.remove(enchantmentToRemove);
            EnchantmentHelper.setEnchantments(newEnchantments, stack);
        }
    }

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void isEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.is(Items.GOLDEN_APPLE)||stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), cancellable = true)
    public void getEnchantmentValue(CallbackInfoReturnable<Integer> cir){
        if (this.getSlef() == Items.GOLDEN_APPLE || this.getSlef() == Items.ENCHANTED_GOLDEN_APPLE) {
            cir.setReturnValue(5);
            cir.cancel();
        }
    }


    @Override
    @Nullable
    public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        FoodProperties original = this.foodProperties;
        if (original == null) return null;

        int bountifulLevel = stack.getEnchantmentLevel(AppleEnchantments.BOUNTIFUL.get());
        int energeticLevel = stack.getEnchantmentLevel(AppleEnchantments.ENERGETIC.get());
        int glintLevel = stack.getEnchantmentLevel(AppleEnchantments.GLINT.get());
        int baneOfEdenLevel = stack.getEnchantmentLevel(AppleEnchantments.BANE_OF_EDEN.get());
        int ampLevel = stack.getEnchantmentLevel(AppleEnchantments.AMPLIFICATION.get());

        if (bountifulLevel <= 0 && energeticLevel <= 0 && glintLevel <= 0 && baneOfEdenLevel <= 0 && ampLevel <= 0) {
            return original;
        }

        int finalNutrition = original.getNutrition();
        float finalSaturation = original.getSaturationModifier();

        // 丰食 提升金苹果的营养值和饱食度，每级增加 3+等级*2
        if (bountifulLevel > 0) {
            finalNutrition += 3 + bountifulLevel * 2;
            finalSaturation = Math.min(1.0F, finalSaturation + (0.3F + bountifulLevel * 0.2F));
        }

        List<Pair<MobEffectInstance, Float>> effects = new ArrayList<>(original.getEffects());

        // 精力充沛 提供力量(等级=附魔等级-1)、速度和急迫效果
        if (energeticLevel > 0) {
            effects.add(Pair.of(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000, energeticLevel-1), 1.0F));
            effects.add(Pair.of(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 500, 0), 1.0F));
            effects.add(Pair.of(new MobEffectInstance(MobEffects.DIG_SPEED, 4000, 0), 1.0F));
        }

        // 金闪闪 提供夜视(基础5分钟，每级+25%)和发光(20秒)效果
        if (glintLevel > 0) {
            int nvDuration = (int) (6000 * (1.0 + 0.25 * glintLevel));
            effects.add(Pair.of(new MobEffectInstance(MobEffects.NIGHT_VISION, nvDuration, 0), 1.0F));
            effects.add(Pair.of(new MobEffectInstance(MobEffects.GLOWING, 460, glintLevel - 1), 1.0F));
        }

        // 伊甸园之殇 负面诅咒，附加饥饿和反胃效果
        if (baneOfEdenLevel > 0) {
            effects.add(Pair.of(new MobEffectInstance(MobEffects.HUNGER, 460, baneOfEdenLevel + 1), 1.0F));
            effects.add(Pair.of(new MobEffectInstance(MobEffects.CONFUSION, 260, 0), 1.0F));
        }

        // 充分吸收 强化所有药水效果，每级持续时间+20%、等级+0.5
        if (ampLevel > 0) {
            effects = effects.stream()
                    .map(pair -> {
                        MobEffectInstance effect = pair.getFirst();
                        int newDuration = (int) (effect.getDuration() * (1.0 + 0.2 * ampLevel));
                        int newAmplifier = (int) Math.min(255, effect.getAmplifier() + ampLevel * 0.5F);
                        MobEffectInstance enhanced = new MobEffectInstance(
                                effect.getEffect(), newDuration, newAmplifier,
                                effect.isAmbient(), effect.isVisible()
                        );
                        return Pair.of(enhanced, pair.getSecond());
                    })
                    .collect(java.util.stream.Collectors.toList());
        }

        return new FoodProperties(
                finalNutrition,
                finalSaturation,
                original.isMeat(),
                original.canAlwaysEat(),
                original.isFastFood(),
                effects
        );
    }

    @Unique
    private Item getSlef(){
        return (Item)(Object)this;
    }
}