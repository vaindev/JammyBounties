package me.vaindev.jammybounties.Utils;

import me.vaindev.jammybounties.Bounty;
import me.vaindev.jammybounties.JammyBounties;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class GuiItemUtil {

    public static final Map<Inventory, Integer> guiPages = new HashMap<>();

    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        meta.setCustomModelData(JammyBounties.INV_ITEM_DATA);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createHeadGuiItem(UUID uuid) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) head.getItemMeta();

        OfflinePlayer user = Bukkit.getOfflinePlayer(uuid);
        skull.setOwningPlayer(user);
        skull.setDisplayName(ChatColor.YELLOW + (net.md_5.bungee.api.ChatColor.BOLD + "WANTED: " + ChatColor.YELLOW + user.getName()));
        skull.setCustomModelData(JammyBounties.INV_ITEM_DATA);
        head.setItemMeta(skull);
        return head;
    }

    public static ItemStack createHeadGuiItem(String user, final String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) head.getItemMeta();

        skull.setDisplayName(name);
        skull.setOwner(user);
        head.setItemMeta(skull);
        return head;
    }

    public static ItemStack createHeadGuiItem(Bounty bounty, String currencySymbol) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        UUID uuid = bounty.getUuid();
        ItemStack[] items = bounty.getItems();
        double eco = bounty.getEco();
        SkullMeta skull = (SkullMeta) head.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Reward Amount: " + ChatColor.YELLOW + currencySymbol + Math.round(eco));
        lore.add(ChatColor.GREEN + "Items: " + ChatColor.YELLOW + items.length + ChatColor.GRAY + " (Click to see more info)");

        OfflinePlayer user = Bukkit.getOfflinePlayer(uuid);
        skull.setOwningPlayer(user);
        skull.setDisplayName(user.getName() + "'s Bounty");
        skull.setLore(lore);

        head.setItemMeta(skull);
        return head;
    }
}
