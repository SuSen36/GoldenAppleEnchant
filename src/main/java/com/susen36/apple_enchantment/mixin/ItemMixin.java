package com.susen36.apple_enchantment.mixin;

import com.susen36.apple_enchantment.AppleEnchantment;
import com.susen36.apple_enchantment.AppleEnchantments;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = Item.class)
public abstract class ItemMixin implements IForgeItem {

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
                    ResourceLocation advId =                                              new ResourceLocation(AppleEnchantment.MODID, "aura_of_gold");
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

    @Unique
    private Item getSlef(){
        return (Item)(Object)this;
    }
}