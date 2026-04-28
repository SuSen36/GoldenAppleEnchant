package com.susen36.apple_enchantment.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {

    @Shadow
    @Final
    Container repairSlots;

    @Shadow
    @Final
    private Container resultSlots;

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 0)
    )
    private int apple_enchantment$bypassCountCheck0(ItemStack instance) {
        if (isGoldenApple(instance)) {
            return 1;
        }
        return instance.getCount();
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 1)
    )
    private int apple_enchantment$bypassCountCheck1(ItemStack instance) {
        if (isGoldenApple(instance)) {
            return 1;
        }
        return instance.getCount();
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/GrindstoneMenu;mergeEnchants(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack apple_enchantment$mergeEnchantsForApple(GrindstoneMenu menu, ItemStack stack0, ItemStack stack1) {
        if (isGoldenApple(stack0) && isGoldenApple(stack1)) {
            return mergeAppleEnchants(stack0, stack1);
        }
        return menu.mergeEnchants(stack0, stack1);
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/GrindstoneMenu;removeNonCurses(Lnet/minecraft/world/item/ItemStack;II)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack apple_enchantment$removeNonCursesForApple(GrindstoneMenu menu, ItemStack source, int damage, int count) {
        if (isGoldenApple(source)) {
            ItemStack input0 = this.repairSlots.getItem(0);
            ItemStack input1 = this.repairSlots.getItem(1);
            boolean bothApples = isGoldenApple(input0) && isGoldenApple(input1);
            int totalCount = (isGoldenApple(input0) ? input0.getCount() : 0) + (isGoldenApple(input1) ? input1.getCount() : 0);
            if (bothApples) {
                // 双槽合并：mergeAppleEnchants 已处理附魔保留逻辑，只设置数量
                source.setCount(totalCount);
                return source;
            }
            // 单槽：移除所有非诅咒附魔
            return removeNonCursesForSingleApple(source, totalCount);
        }
        return menu.removeNonCurses(source, damage, count);
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamageableItem()Z")
    )
    private boolean apple_enchantment$handleAppleNotDamageable(ItemStack instance) {
        if (isGoldenApple(instance)) {
            return false;
        }
        return instance.isDamageableItem();
    }

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0)
    )
    private boolean apple_enchantment$bypassMatchesForApple(ItemStack left, ItemStack right) {
        if (isGoldenApple(left) && isGoldenApple(right)) {
            return true;
        }
        return ItemStack.matches(left, right);
    }

    private static boolean isGoldenApple(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE));
    }

    private static ItemStack mergeAppleEnchants(ItemStack stack0, ItemStack stack1) {
        ItemStack result = stack0.copy();
        Map<Enchantment, Integer> enchants0 = EnchantmentHelper.getEnchantments(stack0);
        Map<Enchantment, Integer> enchants1 = EnchantmentHelper.getEnchantments(stack1);

        // 合并规则：同附魔同等级→保留；同附魔不同等级→取最低；不同附魔→移除
        Map<Enchantment, Integer> merged = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : enchants0.entrySet()) {
            Enchantment ench = entry.getKey();
            int level0 = entry.getValue();
            if (ench.isCurse()) {
                // 诅咒始终保留
                int level1 = enchants1.getOrDefault(ench, 0);
                merged.put(ench, Math.max(level0, level1));
            } else if (enchants1.containsKey(ench)) {
                // 同附魔：同等级保留，不同等级取最低
                int level1 = enchants1.get(ench);
                merged.put(ench, Math.min(level0, level1));
            }
            // 不同附魔（仅存在于stack0）→移除，不加入merged
        }
        // 诅咒仅存在于stack1的也需要保留
        for (Map.Entry<Enchantment, Integer> entry : enchants1.entrySet()) {
            Enchantment ench = entry.getKey();
            if (ench.isCurse() && !merged.containsKey(ench)) {
                merged.put(ench, entry.getValue());
            }
        }

        result.removeTagKey("Enchantments");
        result.removeTagKey("StoredEnchantments");
        result.removeTagKey("RepairCost");
        EnchantmentHelper.setEnchantments(merged, result);
        return result;
    }

    private static ItemStack removeNonCursesForSingleApple(ItemStack source, int count) {
        ItemStack result = source.copy();
        result.removeTagKey("Enchantments");
        result.removeTagKey("StoredEnchantments");
        result.removeTagKey("RepairCost");

        Map<Enchantment, Integer> curses = EnchantmentHelper.getEnchantments(source).entrySet().stream()
                .filter(entry -> entry.getKey().isCurse())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        EnchantmentHelper.setEnchantments(curses, result);
        result.setCount(count);
        return result;
    }
}