package com.susen36.apple_enchantment.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public class GrindstoneResultSlotMixin {

    @Inject(method = "getExperienceFromItem", at = @At("RETURN"), cancellable = true, remap = false)
    private void apple_enchantment$multiplyExpForApple(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            cir.setReturnValue(cir.getReturnValueI() * stack.getCount());
        }
    }
}