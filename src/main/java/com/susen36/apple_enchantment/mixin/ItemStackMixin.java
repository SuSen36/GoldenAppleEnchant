package com.susen36.apple_enchantment.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "matches(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
    private void onMatchesHead(ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        sortEnchantments(getSlef());
        sortEnchantments(other);
    }

    @Unique
    private static void sortEnchantments(ItemStack itemStack) {
        if (itemStack == null || !itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) return;

        if (itemStack.getTag() != null && itemStack.hasTag() && itemStack.getTag().contains("Enchantments", 9)) {
            ListTag list = itemStack.getTag().getList("Enchantments", 10);
            list.sort(Comparator.comparing(t -> ((CompoundTag) t).getString("id")));
        }
    }

    @Unique
    private ItemStack getSlef(){
        return (ItemStack)(Object)this;
    }
}