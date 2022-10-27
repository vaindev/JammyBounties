package me.vaindev.jammybounties.Inventories;

import me.vaindev.jammybounties.Bounty;
import me.vaindev.jammybounties.DataAccess;
import me.vaindev.jammybounties.Utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

import static me.vaindev.jammybounties.Utils.GuiItemUtil.createGuiItem;
import static me.vaindev.jammybounties.Utils.GuiItemUtil.createHeadGuiItem;

public class BountyViewGui {

    private final Inventory inventory;
    private final Plugin plugin;

    public BountyViewGui(Plugin plugin, UUID wantedPlayer) {
        this.plugin = plugin;
        this.inventory = initialiseViewBountyGui(wantedPlayer);
    }

    public Inventory getInventory() {
        return this.inventory;
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
}
