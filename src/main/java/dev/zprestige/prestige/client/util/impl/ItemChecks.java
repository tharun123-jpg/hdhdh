package dev.zprestige.prestige.client.util.impl;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Set;

/**
 * Item classification helpers.
 * The old typed item classes (SwordItem/PickaxeItem) no longer exist in 1.21.11,
 * so we classify by explicit item sets / data components.
 */
public class ItemChecks {
    private static final Set<Item> SWORDS = Set.of(
            Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
            Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
    private static final Set<Item> AXES = Set.of(
            Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE,
            Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
    private static final Set<Item> PICKAXES = Set.of(
            Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE,
            Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);

    public static boolean isSword(ItemStack stack) {
        return stack != null && SWORDS.contains(stack.getItem());
    }

    public static boolean isAxe(ItemStack stack) {
        return stack != null && AXES.contains(stack.getItem());
    }

    public static boolean isSwordOrAxe(ItemStack stack) {
        return isSword(stack) || isAxe(stack);
    }

    public static boolean isPickaxe(ItemStack stack) {
        return stack != null && PICKAXES.contains(stack.getItem());
    }

    public static boolean isArmor(ItemStack stack) {
        return stack != null && stack.contains(DataComponentTypes.EQUIPPABLE);
    }

    public static boolean isFood(ItemStack stack) {
        return stack != null && stack.contains(DataComponentTypes.FOOD);
    }

    public static RegistryEntry<Enchantment> entry(net.minecraft.registry.RegistryKey<Enchantment> key) {
        return Registries.ENCHANTMENT.entryOf(key);
    }

    /**
     * Sums the Protection enchantment level across all armor pieces (client-side
     * replacement for the removed EnchantmentHelper#getProtectionAmount).
     */
    public static int getProtectionLevel(LivingEntity entity) {
        int level = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack piece = entity.getEquippedStack(slot);
            if (piece.isEmpty()) {
                continue;
            }
            ItemEnchantmentsComponent component = piece.get(DataComponentTypes.ENCHANTMENTS);
            if (component != null) {
                level += component.getLevel(entry(Enchantments.PROTECTION));
            }
        }
        return level;
    }
}
