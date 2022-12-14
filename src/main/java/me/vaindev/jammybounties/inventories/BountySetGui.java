package me.vaindev.jammybounties.inventories;

import me.vaindev.jammybounties.JammyBounties;
import me.vaindev.jammybounties.utils.StringFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.vaindev.jammybounties.utils.GuiItemUtil.createGuiItem;
import static me.vaindev.jammybounties.utils.GuiItemUtil.createHeadGuiItem;

public class BountySetGui implements InventoryHolder {

    private final Inventory inventory;
    private final UUID wantedPlayer;

    public BountySetGui(JammyBounties plugin, UUID wantedPlayer) {
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
            inventory.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, Component.text(" ")));
        }

        inventory.setItem(2, createGuiItem(
                Material.GOLD_NUGGET,
                Component.text(ChatColor.DARK_GREEN + "Set Cash Reward"),
                Component.text(ChatColor.GREEN + "Current Reward: "), Component.text(ChatColor.GRAY + "Click to Enter/ Change Cash Reward")));
        inventory.setItem(6, createGuiItem(
                Material.EMERALD,
                Component.text(ChatColor.DARK_GREEN + "Set Bounty"),
                Component.text(ChatColor.GRAY + "Click to Confirm")));
        inventory.setItem(4, createHeadGuiItem(this.wantedPlayer));
    }
}
