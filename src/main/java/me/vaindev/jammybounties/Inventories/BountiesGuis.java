package me.vaindev.jammybounties.Inventories;

import me.vaindev.jammybounties.Bounty;
import me.vaindev.jammybounties.DataAccess;
import me.vaindev.jammybounties.Utils.Pagination;
import me.vaindev.jammybounties.Utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static me.vaindev.jammybounties.Utils.GuiItemUtil.*;

public class BountiesGuis implements InventoryHolder {

    private final List<Inventory> inventories = new ArrayList<>();
    private final Pagination<Bounty> pagination;
    private final List<Bounty> bounties;
    private final Plugin plugin;

    private final int pageSize = 27;

    public BountiesGuis(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.bounties = DataAccess.getBounties();
        this.pagination = new Pagination<>(player, this.pageSize, this.bounties);
        initGui();
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventories.get(this.pagination.getPageNumber());
    }

    private void initGui() {
        this.bounties.sort(Comparator.comparing(Bounty::getDateCreated).reversed());

        int i = 0;
        for (int pageNum = 1; pageNum <= pagination.totalPages(); pageNum++) {
            Inventory inventory = Bukkit.createInventory(null, this.pageSize + 9, StringFormat
                    .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("bounties-gui-title")));

            List<Bounty> page = pagination.getPage(pageNum);
            inventory.setItem(this.pageSize + 4, createGuiItem(
                    Material.PAPER,
                    ChatColor.WHITE + "Current Page: " + ChatColor.GRAY + pageNum
            ));
            if (pageNum > 1)
                inventory.setItem(this.pageSize + 3, previousMenuButton());
            if (pageNum * this.pageSize < bounties.size())
                inventory.setItem(this.pageSize + 5, nextMenuButton());
            for (Bounty bounty : page)
                inventory.setItem(i, createHeadGuiItem(bounty, this.plugin.getConfig().getString("currency")));
            i++;

            this.inventories.add(inventory);
        }
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
}
