package com.susen36.apple_enchantment.mixin;

import com.susen36.apple_enchantment.enchantment.AppleEnchantments;
import com.susen36.apple_enchantment.Config;
import net.minecraft.core.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.IntStream;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Final
    @Shadow
    public int[] costs;

    @Final
    @Shadow
    public int[] enchantClue;

    @Shadow @Final private Container enchantSlots;

    @Final
    @Shadow private DataSlot enchantmentSeed;

    @Shadow public abstract void slotsChanged(Container p_39461_);

    @Unique
    private boolean appleEnchantment$isRefreshing = false;

    @Inject(method = "slotsChanged", at = @At("TAIL"))
    private void injectSlotsChanged(Container container, CallbackInfo ci) {
        if (this.appleEnchantment$isRefreshing || this.enchantSlots.isEmpty() || !this.enchantSlots.getItem(0).is(Items.GOLDEN_APPLE) || Config.INFUSING_TREASURE_ONLY.get()) {
            return;
        }

        int infusingEnchantmentId = Registry.ENCHANTMENT.getId(AppleEnchantments.INFUSING.get());

        int[] infusingSlots = IntStream.range(0, 3)
                .filter(slotIndex -> this.enchantClue[slotIndex] == infusingEnchantmentId)
                .toArray();

        if (infusingSlots.length == 0) {
            this.appleEnchantment$isRefreshing = true;
            this.enchantmentSeed.set(this.enchantmentSeed.get() + 1);
            this.slotsChanged(container);
            this.appleEnchantment$isRefreshing = false;
            return;
        }

        if (infusingSlots.length > 1) {
            int slotWithLowestCost = IntStream.of(infusingSlots)
                    .reduce((a, b) -> this.costs[a] < this.costs[b] ? a : b)
                    .orElse(-1);

            IntStream.of(infusingSlots)
                    .filter(slotIndex -> slotIndex != slotWithLowestCost)
                    .forEach(slotIndex -> this.enchantClue[slotIndex] = -1);
        }
    }
}