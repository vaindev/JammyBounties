package me.vaindev.jammybounties.Inventories;

import me.vaindev.jammybounties.Utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.vaindev.jammybounties.Utils.GuiItemUtil.createGuiItem;
import static me.vaindev.jammybounties.Utils.GuiItemUtil.createHeadGuiItem;

public class BountySetGui implements InventoryHolder {

    private final Inventory inventory;
    private final UUID wantedPlayer;

    public BountySetGui(Plugin plugin, UUID wantedPlayer) {
        this.inventory = Bukkit.createInventory(null, 18, StringFormat
                .formatString(plugin.getConfig().getConfigurationSection("lang").getString("setbounty-gui-title")));
        this.wantedPlayer = wantedPlayer;
        initGui();
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }

    public void initGui() {
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        inventory.setItem(2, createGuiItem(
                Material.GOLD_NUGGET,
                ChatColor.DARK_GREEN + "Set Cash Reward",
                ChatColor.GREEN + "Current Reward: ", ChatColor.GRAY + "Click to Enter/ Change Cash Reward"));
        inventory.setItem(6, createGuiItem(
                Material.EMERALD,
                ChatColor.DARK_GREEN + "Set Bounty",
                ChatColor.GRAY + "Click to Confirm"));
        inventory.setItem(4, createHeadGuiItem(this.wantedPlayer));
    }
}
