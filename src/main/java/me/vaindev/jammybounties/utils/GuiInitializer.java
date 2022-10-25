package me.vaindev.jammybounties.utils;

import me.vaindev.jammybounties.Bounty;
import me.vaindev.jammybounties.DataAccess;
import me.vaindev.jammybounties.JammyBounties;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class GuiInitializer {

    public static final Map<Inventory, Integer> guiPages = new HashMap();
    private final Plugin plugin;

    public GuiInitializer(Plugin plugin) {
        this.plugin = plugin;
    }

    public Inventory listBountiesGui(int page) {
        Inventory listGui = initialiseListGui(page);
        return listGui;
    }

    public Inventory setBountyGui(UUID wantedPlayer) {
        Inventory setGui = initialiseSetBountyGui(wantedPlayer);
        return setGui;
    }

    public Inventory viewBountyGui(UUID wantedPlayer) {
        Inventory viewGui = initialiseViewBountyGui(wantedPlayer);
        return viewGui;
    }

    public Inventory initialiseListGui(int page) {
        int pageSize = 27;

        Inventory gui = Bukkit.createInventory(null, pageSize + 9, StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("bounties-gui-title")));

        List<Bounty> bounties = DataAccess.getBounties();
        bounties.sort(Comparator.comparing(Bounty::getDateCreated).reversed());

        List<Bounty> selectBounties = bounties.subList((page - 1) * pageSize,
                bounties.size() < pageSize ? bounties.size() : (page * pageSize - 1));

        gui.setItem(31, createGuiItem(
                Material.PAPER,
                ChatColor.WHITE + "Current Page: " + ChatColor.GRAY + page
        ));
        if (page > 1)
            gui.setItem(30, previousMenuButton());
        if (page * pageSize < bounties.size())
            gui.setItem(32, nextMenuButton());

        int i = 0;
        for (Bounty bounty : selectBounties) {
            gui.setItem(i, this.createHeadGuiItem(bounty));
            i++;
        }

        guiPages.put(gui, page);
        return gui;
    }

    public Inventory initialiseSetBountyGui(UUID wantedPlayer) {
        Inventory gui = Bukkit.createInventory(null, 18, StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("setbounty-gui-title")));

        for (int i = 0; i < 9; i++) {
            gui.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        gui.setItem(2, createGuiItem(
                Material.GOLD_NUGGET,
                ChatColor.DARK_GREEN + "Set Cash Reward",
                new String[]{ ChatColor.GREEN + "Current Reward: ", ChatColor.GRAY + "Click to Enter/ Change Cash Reward"}));
        gui.setItem(6, createGuiItem(
                Material.EMERALD,
                ChatColor.DARK_GREEN + "Set Bounty",
                ChatColor.GRAY + "Click to Confirm"));
        gui.setItem(4, createHeadGuiItem(wantedPlayer));

        return gui;
    }

    public Inventory initialiseViewBountyGui(UUID wantedPlayer) {
        Inventory gui = Bukkit.createInventory(null, 27, StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("viewbounty-gui-title")));

        for (int i = 0; i < 27; i++) {
            gui.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        for (int i = 9; i < 18; i++) {
            gui.setItem(i, createGuiItem(Material.MAGENTA_STAINED_GLASS_PANE, " "));
        }

        gui.setItem(4, createHeadGuiItem(wantedPlayer));

        Bounty bounty = DataAccess.getBounty(wantedPlayer);

        ItemStack[] items = null;
        double eco = 0;

        if (bounty != null) {
            items = bounty.getItems();
            eco = bounty.getEco();
        }

        gui.setItem(22, createGuiItem(Material.GOLD_NUGGET,
                ChatColor.DARK_GREEN + "Cash Reward",
                ChatColor.GOLD + this.plugin.getConfig().getString("currency") + eco));

        int i = 9;
        if (items != null)
            for (ItemStack item: items) {
                gui.setItem(i, item);
                i++;
            }

        return gui;
    }

    private static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
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

    public static ItemStack previousMenuButton() {
        return createHeadGuiItem(
                "MHF_ArrowLeft",
                ChatColor.GREEN + "Previous Page"
        );
    }

    public static ItemStack nextMenuButton() {
        return createHeadGuiItem(
                "MHF_ArrowRight",
                ChatColor.GREEN + "Next Page"
        );
    }

    public ItemStack createHeadGuiItem(Bounty bounty) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        UUID uuid = bounty.getUuid();
        ItemStack[] items = bounty.getItems();
        double eco = bounty.getEco();
        SkullMeta skull = (SkullMeta) head.getItemMeta();

        ArrayList<String> lore = new ArrayList();
        lore.add(ChatColor.GREEN + "Reward Amount: " + ChatColor.YELLOW + this.plugin.getConfig().getString("currency") + Math.round(eco));
        lore.add(ChatColor.GREEN + "Items: " + ChatColor.YELLOW + items.length + ChatColor.GRAY + " (Click to see more info)");

        OfflinePlayer user = Bukkit.getOfflinePlayer(uuid);
        skull.setOwningPlayer(user);
        skull.setDisplayName(user.getName() + "'s Bounty");
        skull.setLore(lore);

        head.setItemMeta(skull);
        return head;
    }
}
