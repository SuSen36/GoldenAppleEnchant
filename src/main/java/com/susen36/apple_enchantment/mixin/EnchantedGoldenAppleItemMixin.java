package com.susen36.apple_enchantment.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedGoldenAppleItem;
import net.minecraft.world.item.Vanishable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 通过Mixin为附魔金苹果实现Vanishable接口。
 * * 原理说明：
 * Minecraft 的“消失诅咒” (Curse of Vanishing) 附魔检查机制依赖于 Vanishable 标记接口。
 * 只要一个物品类实现了这个接口，游戏底层的附魔验证逻辑 (canEnchant) 就会将其判定为
 * “可消失物品”，从而允许在铁砧上将“消失诅咒”附魔到该物品上。
 */
@Mixin(EnchantedGoldenAppleItem.class)
public abstract class EnchantedGoldenAppleItemMixin implements Vanishable {
    //NULL
}
