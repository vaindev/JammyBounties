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

import java.util.Comparator;
import java.util.List;

import static me.vaindev.jammybounties.Utils.GuiItemUtil.*;

public class BountiesGui {

    private final Inventory inventory;
    private final Plugin plugin;
    private final int page;
    private static final int pageSize = 27;

    public BountiesGui (Plugin plugin, int page) {
        this.plugin = plugin;
        this.page = page;
        this.inventory = Bukkit.createInventory(null, pageSize + 9, StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("bounties-gui-title")));
        initGui();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    private void initGui() {
        int pageSize = 27;

        List<Bounty> bounties = DataAccess.getBounties();
        bounties.sort(Comparator.comparing(Bounty::getDateCreated).reversed());

        List<Bounty> selectBounties = bounties.subList((page - 1) * pageSize,
                bounties.size() < pageSize ? bounties.size() : (page * pageSize - 1));

        inventory.setItem(31, createGuiItem(
                Material.PAPER,
                ChatColor.WHITE + "Current Page: " + ChatColor.GRAY + page
        ));
        if (page > 1)
            inventory.setItem(30, previousMenuButton());
        if (page * pageSize < bounties.size())
            inventory.setItem(32, nextMenuButton());

        int i = 0;
        for (Bounty bounty : selectBounties) {
            inventory.setItem(i, createHeadGuiItem(bounty, this.plugin.getConfig().getString("currency")));
            i++;
        }

        guiPages.put(inventory, page);
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
