package dev.duel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/** Выбор кита для дуэли (3 кита). */
public class KitMenu implements Listener {

    public static class Holder implements InventoryHolder {
        Inventory inv;
        @Override public Inventory getInventory() { return inv; }
    }

    private static final int[] KIT_SLOTS = {11, 13, 15};
    private static final int BACK = 22;

    private final DuelPlugin plugin;
    private final DuelManager manager;
    private final DuelMenu menu;

    public KitMenu(DuelPlugin plugin, DuelManager manager, DuelMenu menu) {
        this.plugin = plugin;
        this.manager = manager;
        this.menu = menu;
    }

    public void open(Player p) {
        Holder h = new Holder();
        h.inv = Bukkit.createInventory(h, 27, plugin.raw("kit-menu-title"));
        render(h);
        p.openInventory(h.inv);
        p.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.5f, 0.8f);
    }

    private void render(Holder h) {
        Inventory inv = h.inv;
        ItemStack black = item(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack gray = item(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            boolean border = i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8;
            inv.setItem(i, border ? (i % 2 == 0 ? black : gray) : black);
        }
        inv.setItem(4, item(Material.NETHERITE_SWORD, "&4&lДуэль с китами",
                "&7Выберите кит. Бой пройдёт на отдельной",
                "&7арене высоко в небе, кит выдаётся",
                "&7автоматически и пропадает после дуэли."));

        for (int i = 0; i < KIT_SLOTS.length; i++) {
            int id = i + 1;
            Material icon = Material.matchMaterial(plugin.getConfig().getString("kits.list." + id + ".icon", "IRON_SWORD"));
            if (icon == null) icon = Material.IRON_SWORD;
            List<String> lore = new ArrayList<>();
            for (String l : plugin.getConfig().getStringList("kits.list." + id + ".lore")) lore.add(l);
            lore.add("");
            if (manager.kits().isDefined(id)) {
                lore.add("&7Содержимое:");
                Map<String, Integer> agg = new LinkedHashMap<>();
                for (ItemStack it : manager.kits().allItems(id)) {
                    agg.merge(nameOf(it), it.getAmount(), Integer::sum);
                }
                int shown = 0;
                for (Map.Entry<String, Integer> en : agg.entrySet()) {
                    if (shown++ >= 9) { lore.add("&8  …и ещё " + (agg.size() - 9)); break; }
                    lore.add("&8▪ &f" + en.getKey() + (en.getValue() > 1 ? " &7x" + en.getValue() : ""));
                }
                lore.add("");
                lore.add("&eНажмите, чтобы создать вызов");
            } else {
                lore.add("&cКит пока не настроен");
            }
            inv.setItem(KIT_SLOTS[i], item(icon, manager.kitName(id), lore.toArray(new String[0])));
        }
        inv.setItem(BACK, item(Material.ARROW, "&7Назад", "&8К списку дуэлей"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof Holder)) return;
        e.setCancelled(true);
        if (e.getClickedInventory() == null || e.getClickedInventory() != e.getInventory()) return;
        final Player p = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

        if (slot == BACK) {
            p.closeInventory();
            Bukkit.getScheduler().runTask(plugin, () -> menu.open(p));
            return;
        }
        for (int i = 0; i < KIT_SLOTS.length; i++) {
            if (slot != KIT_SLOTS[i]) continue;
            p.closeInventory();
            manager.createOpen(p, DuelManager.Mode.KIT, i + 1);
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 0.7f);
            Bukkit.getScheduler().runTask(plugin, () -> menu.open(p));   // вернуть в общий список — там виден свой вызов
            return;
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof Holder) e.setCancelled(true);
    }

    // ---------------------------------------------------------------- helpers

    private String nameOf(ItemStack it) {
        ItemMeta im = it.getItemMeta();
        if (im != null && im.hasDisplayName()) return ChatColor.stripColor(im.getDisplayName());
        String[] parts = it.getType().name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : parts) {
            if (s.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
        }
        return sb.toString();
    }

    private String c(String s) { return ChatColor.translateAlternateColorCodes('&', s); }

    private ItemStack item(Material m, String name, String... lore) {
        ItemStack i = new ItemStack(m);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(c(name));
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(c(s));
        im.setLore(l);
        im.addItemFlags(ItemFlag.values());
        i.setItemMeta(im);
        return i;
    }
}
