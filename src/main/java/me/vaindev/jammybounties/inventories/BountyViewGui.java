package me.vaindev.jammybounties.inventories;

import me.vaindev.jammybounties.Bounty;
import me.vaindev.jammybounties.DataAccess;
import me.vaindev.jammybounties.JammyBounties;
import me.vaindev.jammybounties.utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.vaindev.jammybounties.utils.GuiItemUtil.createGuiItem;
import static me.vaindev.jammybounties.utils.GuiItemUtil.createHeadGuiItem;

public class BountyViewGui implements InventoryHolder {

    private final Inventory inventory;
    private final UUID wantedPlayer;
    private final JammyBounties plugin;

    public BountyViewGui(JammyBounties plugin, UUID wantedPlayer) {
        this.plugin = plugin;
        this.wantedPlayer = wantedPlayer;
        this.inventory = initialiseViewBountyGui();
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }

    public Inventory initialiseViewBountyGui() {
        Inventory gui = Bukkit.createInventory(null, 27, StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("viewbounty-gui-title")));

        for (int i = 0; i < 27; i++) {
            gui.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        for (int i = 9; i < 18; i++) {
            gui.setItem(i, createGuiItem(Material.MAGENTA_STAINED_GLASS_PANE, " "));
        }

        gui.setItem(4, createHeadGuiItem(this.wantedPlayer));

        Bounty bounty = DataAccess.getBounty(this.wantedPlayer);

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
