package com.susen36.apple_enchantment.mixin;

import com.susen36.apple_enchantment.AppleEnchantment;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;", at = @At("TAIL"))
    private void onEat(Level level, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (this.getSlef() instanceof ServerPlayer player) {
            if (!EnchantmentHelper.getEnchantments(stack).isEmpty() && EnchantmentHelper.getEnchantments(stack).keySet().stream().noneMatch(Enchantment::isCurse)) {
                ResourceLocation advId = ResourceLocation.fromNamespaceAndPath(AppleEnchantment.MODID, "overpowered");
                Advancement advancement = player.getServer().getAdvancements().getAdvancement(advId);

                if (advancement != null) {
                    player.getAdvancements().award(advancement, "requirement");
                }
            }
        }
    }

    @Unique
    private LivingEntity getSlef() {
        return (LivingEntity) (Object) this;
    }
}