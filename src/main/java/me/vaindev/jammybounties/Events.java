package me.vaindev.jammybounties;

import com.comphenix.protocol.PacketType;
import me.vaindev.jammybounties.utils.GuiInitializer;
import me.vaindev.jammybounties.utils.StringFormat;
import org.bukkit.*;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.List;

public class Events implements Listener {

    private static final Map<Player, Inventory> setBountyMenuMap = new HashMap();
    private static final Set<Player> successfullySetBounty = new HashSet<>();
    private final Plugin plugin;
    private final JammyBounties jammyBounties;
    private final GuiInitializer guiInitializer;

    public Events(Plugin plugin, JammyBounties jammyBounties, GuiInitializer guiInitializer) {
        this.plugin = plugin;
        this.jammyBounties = jammyBounties;
        this.guiInitializer = guiInitializer;
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();

        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();

        if (view.getTitle().equals(StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("viewbounty-gui-title")))) {
            event.setCancelled(true);
            return;
        }

        if (view.getTitle().equals(StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("bounties-gui-title")))) {

            ItemStack currentItem = event.getCurrentItem();

            int page;
            if (currentItem == null)
                return;
            if (currentItem.equals(this.guiInitializer.previousMenuButton())) {
                page = guiInitializer.guiPages.get(view.getTopInventory()) - 1;
                guiInitializer.guiPages.remove(view.getTopInventory());
                player.openInventory(this.guiInitializer.listBountiesGui(page));
                return;
            }
            if (currentItem.equals(this.guiInitializer.nextMenuButton())) {
                page = guiInitializer.guiPages.get(view.getTopInventory()) + 1;
                guiInitializer.guiPages.remove(view.getTopInventory());
                player.openInventory(this.guiInitializer.listBountiesGui(page));
                return;
            }
            if (!currentItem.getType().equals(Material.PLAYER_HEAD)) {
                event.setCancelled(true);
                return;
            }

            SkullMeta skull = (SkullMeta) currentItem.getItemMeta();

            player.openInventory(this.guiInitializer.viewBountyGui(skull.getOwningPlayer().getUniqueId()));
            return;
        }

        if (view.getTitle().equals(StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("setbounty-gui-title")))) {
            double eco = 0;

            Inventory inv = event.getClickedInventory();

            if (event.getSlot() < 9 &&
                    !(inv instanceof PlayerInventory)) {
                event.setCancelled(true);
            }

            if (event.getSlot() == 2 &&
                    !(inv instanceof PlayerInventory)) {
                this.setBountyMenuMap.put(player, event.getClickedInventory());
                openSign(player);
                return;
            }

            if (event.getSlot() == 6 &&
                    !(event.getClickedInventory() instanceof PlayerInventory)) {
                ItemStack[] items = filterInvItems(inv.getContents());

                SkullMeta skull;
                try {
                    ItemStack head = inv.getItem(4);
                    skull = (SkullMeta) head.getItemMeta();
                } catch (Exception e) {
                    StringFormat.msg(player, this.plugin.getConfig().getString("prefix") + plugin
                            .getConfig()
                            .getConfigurationSection("lang")
                            .getString("unknown-error"));
                    event.getView().close();
                    return;
                }
                UUID target = skull.getOwningPlayer().getUniqueId();

                List<String> lore = inv.getItem(2).getItemMeta().getLore();
                String[] ecoString;
                try {
                    ecoString = lore.get(0).split(this.plugin.getConfig().getString("currency"));
                    if (ecoString.length > 1)
                        eco = Integer.valueOf(ecoString[1]);
                } catch (NumberFormatException e) {
                    StringFormat.msg(player, this.plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-eco-reward"));
                }

                if (!JammyBounties.economy.has(player, eco)) {
                    StringFormat.msg(player, this.plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("insufficient-funds"));
                    event.getView().close();
                    return;
                }
                JammyBounties.economy.withdrawPlayer(player, eco);

                String announcementString;
                if (DataAccess.getBounty(target) != null){
                    DataAccess.appendBounty(target, items, eco);
                    announcementString = this.plugin.getConfig().getConfigurationSection("lang").getString("bounty-appended");
                } else {
                    DataAccess.setBounty(target, items, eco);
                    announcementString = this.plugin.getConfig().getConfigurationSection("lang").getString("bounty-set");
                }
                successfullySetBounty.add(player);
                event.getView().close();

                Bounty bounty = new Bounty(target, items, eco, null);
                String rewardString = bounty.getRewardsAsString(plugin.getConfig().getString("currency"));

                Player targetPlayer = Bukkit.getPlayer(target);
                announcementString = announcementString
                        .replaceAll("(?i)\\{player\\}", player.getDisplayName())
                        .replaceAll("(?i)\\{target\\}",  targetPlayer != null ? targetPlayer.getDisplayName() : Bukkit.getOfflinePlayer(target).getName())
                        .replaceAll("(?i)\\{rewards\\}", rewardString);
                StringFormat.msg(player, this.plugin.getConfig().getString("prefix") + announcementString);

                return;
            }
        }
    }

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(StringFormat
                .formatString(plugin.getConfig().getConfigurationSection("lang").getString("setbounty-gui-title"))))
            return;
        if (this.successfullySetBounty.remove(event.getPlayer()))
            return;

        ItemStack[] items = filterInvItems(event.getInventory().getContents());
        event.getPlayer().getInventory().addItem(items);
    }

    @EventHandler
    public void OnPlayerKill(PlayerDeathEvent event) {
        Entity killed = event.getEntity();
        Entity killer = event.getEntity().getKiller();

        if (!(killed instanceof Player))
            return;
        if (!(killer instanceof Player))
            return;

        this.jammyBounties.claimBounty((Player) killed, (Player) killer);
    }

    public void openSign(Player target) {
        SignMenuFactory.Menu menu = JammyBounties.signMenuFactory.newMenu(Arrays.asList("", "^^^^^^^^", "Enter reward", "amount above"))
                .reopenIfFail(false)
                .response((player, strings) -> {
                    String eco = strings[0];

                    Inventory inv = this.setBountyMenuMap.get(player);
                    this.setBountyMenuMap.remove(player);

                    ItemStack ecoSet = inv.getItem(2);
                    ItemMeta meta = ecoSet.getItemMeta();
                    if (meta.getLore().size() > 1) {
                        List<String> newLore = meta.getLore();
                        newLore.set(0, ChatColor.DARK_GREEN + "Current Reward: " + ChatColor.GOLD + plugin.getConfig().getString("currency") + eco);
                        meta.setLore(newLore);
                    }
                    ecoSet.setItemMeta(meta);
                    inv.setItem(2, ecoSet);

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> player.openInventory(inv), 2L);
                    return true;
                });

        menu.open(target);
    }

    private static ItemStack[] filterInvItems(ItemStack[] items) {
        return Arrays.stream(items).filter(item -> {
            if (item == null)
                return false;
            if (!item.hasItemMeta())
                return true;
            if (!item.getItemMeta()
                    .hasCustomModelData())
                return true;
            return item.getItemMeta()
                    .getCustomModelData() != JammyBounties.INV_ITEM_DATA;
        }).toArray(ItemStack[]::new);
    }
}
