package me.vaindev.jammybounties.inventories;

import me.vaindev.jammybounties.Bounty;
import me.vaindev.jammybounties.DataAccess;
import me.vaindev.jammybounties.JammyBounties;
import me.vaindev.jammybounties.utils.Pagination;
import me.vaindev.jammybounties.utils.StringFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static me.vaindev.jammybounties.utils.GuiItemUtil.*;

public class BountiesGuis implements InventoryHolder {

    private final List<Inventory> inventories = new ArrayList<>();
    private final Pagination<Bounty> pagination;
    private final List<Bounty> bounties;
    private final JammyBounties plugin;

    private final int pageSize = 27;

    public BountiesGuis(JammyBounties plugin, Player player) {
        this.plugin = plugin;
        this.bounties = DataAccess.getBounties();
        this.bounties.sort(Comparator.comparing(Bounty::getDateCreated).reversed());
        this.pagination = new Pagination<>(player, this.pageSize, this.bounties);
        initGui();
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventories.get(this.pagination.getPageNumber() - 1);
    }

    public Inventory getPreviousInventory() {
        int previousPage = this.pagination.getPageNumber() - 2;
        this.pagination.setPageNumber(previousPage);
        return this.inventories.get(previousPage);

    }

    public Inventory getNextInventory() {
        int nextPage = this.pagination.getPageNumber();
        this.pagination.setPageNumber(nextPage);
        return this.inventories.get(nextPage);
    }

    private void initGui() {
        for (int pageNum = 1; pageNum <= pagination.totalPages(); pageNum++) {
            Inventory inventory = Bukkit.createInventory(null, this.pageSize + 9, StringFormat
                    .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("bounties-gui-title")));

            List<Bounty> page = pagination.getPage(pageNum);

            inventory.setItem(this.pageSize + 4, createGuiItem(
                    Material.PAPER,
                    Component.text(ChatColor.WHITE + "Current Page: " + ChatColor.GRAY + pageNum)
            ));
            if (pageNum > 1)
                inventory.setItem(this.pageSize + 3, previousMenuButton());
            if (pageNum * this.pageSize < bounties.size())
                inventory.setItem(this.pageSize + 5, nextMenuButton());

            for (int i = 0; i < page.size(); i++) {
                Bounty bounty = page.get(i);
                inventory.setItem(i, createHeadGuiItem(bounty, this.plugin.getConfig().getString("currency")));
            }

            this.inventories.add(inventory);
        }
    }

    public static ItemStack previousMenuButton() {
        return createHeadGuiItem(
                "MHF_ArrowLeft",
                Component.text(ChatColor.GREEN + "Previous Page")
        );
    }

    public static ItemStack nextMenuButton() {
        return createHeadGuiItem(
                "MHF_ArrowRight",
                Component.text(ChatColor.GREEN + "Next Page")
        );
    }
}
