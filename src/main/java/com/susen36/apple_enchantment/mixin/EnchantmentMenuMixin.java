package com.susen36.apple_enchantment.mixin;

import com.susen36.apple_enchantment.AppleEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;
import java.util.stream.IntStream;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Final
    @Shadow
    public int[] costs;

    @Final
    @Shadow
    public int[] enchantClue;

    @Redirect(
            method = "slotsChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"
            )
    )
    private void redirectExecute(ContainerLevelAccess instance, BiConsumer<Level, BlockPos> consumer) {
        instance.execute(consumer);

        // instance执行后，过滤重复的注魔附魔
        int infusingEnchantmentId = Registry.ENCHANTMENT.getId(AppleEnchantments.INFUSING.get());

        int[] infusingSlots = IntStream.range(0, 3)
                .filter(slotIndex -> this.enchantClue[slotIndex] == infusingEnchantmentId)
                .toArray();

        if (infusingSlots.length > 1) {
            int slotWithLowestCost = IntStream.of(infusingSlots)
                    .reduce((a, b) -> this.costs[a] < this.costs[b] ? a : b)
                    .orElse(-1);

            IntStream.of(infusingSlots)
                    .filter(slotIndex -> slotIndex != slotWithLowestCost)
                    .forEach(slotIndex -> this.enchantClue[slotIndex] = -1);

            // 再次广播，将过滤后的数据同步到客户端
            this.getSlef().broadcastChanges();
        }
    }

    @Unique
    private EnchantmentMenu getSlef(){
        return (EnchantmentMenu)(Object)this;
    }
}