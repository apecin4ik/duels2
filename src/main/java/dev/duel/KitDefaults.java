package dev.duel;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * Три кита из схематики kit.schem (сундуки). Индекс массива = номер слота в сундуке.
 * Устанавливаются в kits.yml, если кит ещё не настроен, или командой /duel defaultkits.
 */
final class KitDefaults {
    private KitDefaults() { }

    static ItemStack[] get(int id) {
        switch (id) {
            case 1: return starter();
            case 2: return gosudar();
            case 3: return palatin();
            default: return new ItemStack[0];
        }
    }

    // ------------------------------------------------------------ кит 1: кожаный стартовый

    private static ItemStack[] starter() {
        return new ItemStack[]{
                leather(Material.LEATHER_HELMET, 13803743),
                leather(Material.LEATHER_CHESTPLATE, 14919903),
                leather(Material.LEATHER_LEGGINGS, 14919903),
                leather(Material.LEATHER_BOOTS, 14919903),
                new ItemStack(Material.STONE_SWORD),
                new ItemStack(Material.BOW),
                new ItemStack(Material.STONE_AXE),
                new ItemStack(Material.COOKED_PORKCHOP, 16),
                new ItemStack(Material.SHIELD),
                new ItemStack(Material.ARROW, 16)
        };
    }

    // ------------------------------------------------------------ кит 2: Gosudar (алмаз)

    private static ItemStack[] gosudar() {
        String t = "&r&f&lНабор &5&lGosudar";
        return new ItemStack[]{
                named(Material.DIAMOND_HELMET, t, "protection", 4, "unbreaking", 3),
                named(Material.DIAMOND_CHESTPLATE, t, "protection", 4, "unbreaking", 3),
                named(Material.DIAMOND_LEGGINGS, t, "protection", 4, "unbreaking", 3),
                named(Material.DIAMOND_BOOTS, t, "protection", 4, "unbreaking", 3),
                named(Material.DIAMOND_SWORD, t, "sharpness", 4, "fire_aspect", 2, "unbreaking", 2),
                named(Material.BOW, t, "power", 2, "flame", 1, "infinity", 1),
                named(Material.DIAMOND_AXE, t, "efficiency", 4, "unbreaking", 3),
                named(Material.SHIELD, t),
                new ItemStack(Material.GOLDEN_APPLE, 10),
                new ItemStack(Material.COOKED_BEEF, 64),
                new ItemStack(Material.ARROW, 64),
                new ItemStack(Material.TOTEM_OF_UNDYING)
        };
    }

    // ------------------------------------------------------------ кит 3: Palatin (незерит)

    private static ItemStack[] palatin() {
        String t = "&r&f&lНабор &4&lPalatin";
        return new ItemStack[]{
                named(Material.NETHERITE_HELMET, t, "protection", 4, "unbreaking", 3),
                named(Material.NETHERITE_CHESTPLATE, t, "protection", 4, "unbreaking", 3),
                named(Material.NETHERITE_LEGGINGS, t, "protection", 4, "unbreaking", 3),
                named(Material.NETHERITE_BOOTS, t, "protection", 4, "unbreaking", 3),
                named(Material.NETHERITE_SWORD, t, "sharpness", 5, "fire_aspect", 2, "unbreaking", 2),
                named(Material.BOW, t, "power", 4, "flame", 1, "infinity", 1),
                named(Material.NETHERITE_AXE, t, "efficiency", 5, "unbreaking", 3),
                named(Material.SHIELD, t),
                new ItemStack(Material.GOLDEN_APPLE, 16),
                new ItemStack(Material.COOKED_BEEF, 64),
                new ItemStack(Material.TOTEM_OF_UNDYING),
                new ItemStack(Material.TOTEM_OF_UNDYING),
                potion(PotionType.STRENGTH),
                potion(PotionType.STRENGTH),
                potion(PotionType.SPEED),
                potion(PotionType.SPEED)
        };
    }

    // ------------------------------------------------------------ helpers

    private static ItemStack leather(Material m, int rgb) {
        ItemStack i = new ItemStack(m);
        LeatherArmorMeta lm = (LeatherArmorMeta) i.getItemMeta();
        lm.setColor(Color.fromRGB(rgb));
        i.setItemMeta(lm);
        return i;
    }

    /** Предмет с названием и зачарованиями (пары: ключ зачарования, уровень). */
    private static ItemStack named(Material m, String title, Object... ench) {
        ItemStack i = new ItemStack(m);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
        i.setItemMeta(im);
        for (int k = 0; k + 1 < ench.length; k += 2) {
            Enchantment e = Enchantment.getByKey(NamespacedKey.minecraft((String) ench[k]));
            if (e != null) i.addUnsafeEnchantment(e, (Integer) ench[k + 1]);
        }
        return i;
    }

    /** Усиленное зелье (strong_*, уровень II). */
    private static ItemStack potion(PotionType type) {
        ItemStack i = new ItemStack(Material.POTION);
        PotionMeta pm = (PotionMeta) i.getItemMeta();
        pm.setBasePotionData(new PotionData(type, false, true));
        i.setItemMeta(pm);
        return i;
    }
}
