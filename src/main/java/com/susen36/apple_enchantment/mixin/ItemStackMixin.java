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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "isSameItemSameTags", at = @At("HEAD"), cancellable = true)
    private static void onIsSameItemSameTags(ItemStack stackA, ItemStack stackB, CallbackInfoReturnable<Boolean> cir) {
        if (stackA.is(Items.ENCHANTED_GOLDEN_APPLE) && stackB.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            CompoundTag tagA = stackA.getTag();
            CompoundTag tagB = stackB.getTag();

            // 两者都没有标签则视为相同
            if (tagA == null && tagB == null) {
                cir.setReturnValue(true);
                return;
            }

            if (tagA != null && tagB != null) {
                // 复制标签并移除附魔列表，比较剩余NBT是否一致
                CompoundTag copyA = tagA.copy();
                CompoundTag copyB = tagB.copy();

                copyA.remove("Enchantments");
                copyB.remove("Enchantments");

                // 剩余NBT一致且附魔集合内容相同（忽略顺序）则允许堆叠
                if (Objects.equals(copyA, copyB) && getEnchantmentSet(tagA).equals(getEnchantmentSet(tagB))) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    // 将附魔列表转为Set集合，忽略附魔标签顺序
    @Unique
    private static Set<String> getEnchantmentSet(CompoundTag tag) {
        Set<String> enchantSet = new HashSet<>();
        if (tag.contains("Enchantments", 9)) {
            ListTag list = tag.getList("Enchantments", 10);
            for (int i = 0; i < list.size(); i++) {
                enchantSet.add(list.getCompound(i).toString());
            }
        }
        return enchantSet;
    }
}