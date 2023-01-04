package me.vaindev.jammybounties;

import me.vaindev.jammybounties.inventories.BountiesGuis;
import me.vaindev.jammybounties.inventories.BountyViewGui;
import me.vaindev.jammybounties.utils.StringFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
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
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Events implements Listener {

    private static final HashMap<Player, Inventory> setBountyMenuMap = new HashMap<>();
    private static final Set<Player> successfullySetBounty = new HashSet<>();
    private final JammyBounties plugin;

    public Events(JammyBounties plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void OnBountiesGuiClick(InventoryClickEvent event) {
        InventoryView view = event.getView();

        if (!(event.getWhoClicked() instanceof Player player))
            return;

        if (!view.title().equals(StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("bounties-gui-title"))))
            return;

        ItemStack currentItem = event.getCurrentItem();
        BountiesGuis bountiesGuis = new BountiesGuis(plugin, player);

        if (currentItem == null)
            return;
        event.setCancelled(true);
        if (!currentItem.getType().equals(Material.PLAYER_HEAD))
            return;
        if (currentItem.equals(BountiesGuis.previousMenuButton())) {
            player.openInventory(bountiesGuis.getPreviousInventory());
            return;
        }
        if (currentItem.equals(BountiesGuis.nextMenuButton())) {
            player.openInventory(bountiesGuis.getNextInventory());
            return;
        }

        SkullMeta skull = (SkullMeta) currentItem.getItemMeta();

        BountyViewGui viewGui;
        viewGui = new BountyViewGui(plugin, skull.getOwningPlayer().getUniqueId());
        player.openInventory(viewGui.getInventory());
    }

    @EventHandler
    public void OnViewBountyGuiClick(InventoryClickEvent event) {
        InventoryView view = event.getView();

        if (!(event.getWhoClicked() instanceof Player))
            return;

        if (view.title().equals(StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("viewbounty-gui-title")))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnSetBountyGuiClick(InventoryClickEvent event) {
        InventoryView view = event.getView();

        if (!(event.getWhoClicked() instanceof Player player))
            return;

        if (!view.title().equals(StringFormat
                .formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("setbounty-gui-title"))))
            return;

        double eco = 0;

        Inventory inv = event.getClickedInventory();

        if (event.getSlot() < 9 &&
                !(inv instanceof PlayerInventory)) {
            event.setCancelled(true);
        }

        if (event.getSlot() == 2 &&
                !(inv instanceof PlayerInventory)) {
            setBountyMenuMap.put(player, event.getClickedInventory());
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
            UUID targetId = skull.getOwningPlayer().getUniqueId();

            List<Component> lore = inv.getItem(2).getItemMeta().lore();
            String[] ecoString;
            try {
                ecoString = LegacyComponentSerializer.legacyAmpersand().serialize(lore.get(0)).split(this.plugin.getConfig().getString("currency"));
                if (ecoString.length > 1)
                    eco = Double.parseDouble(ecoString[1]);
            } catch (NumberFormatException e) {
                StringFormat.msg(player, this.plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-eco-reward"));
            }

            if (!JammyBounties.economy.has(player, eco)) {
                StringFormat.msg(player, this.plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("insufficient-funds"));
                event.getView().close();
                return;
            }
            JammyBounties.economy.withdrawPlayer(player, eco);

            Component announcement;
            if (DataAccess.getBounty(targetId) != null) {
                if (DataAccess.getBounty(targetId).getItems().length + items.length > 9) {
                    StringFormat.msg(player, this.plugin.getConfig().getString("prefix") + this.plugin.getConfig().getConfigurationSection("lang").getString("too-many-items"));
                    return;
                }
                DataAccess.appendBounty(targetId, items, eco);
                announcement = StringFormat.formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("bounty-appended"));
            } else {
                DataAccess.setBounty(targetId, items, eco);
                announcement = StringFormat.formatString(this.plugin.getConfig().getConfigurationSection("lang").getString("bounty-set"));
            }
            successfullySetBounty.add(player);
            event.getView().close();

            Bounty bounty = new Bounty(targetId, items, eco, null);
            Component rewardsTextComponent = bounty.getRewardsTextComponent(plugin.getConfig().getString("currency"));

            Player targetPlayer = Bukkit.getPlayer(targetId);
            Component targetName = targetPlayer != null ? targetPlayer.displayName() : Component.text(Bukkit.getOfflinePlayer(targetId).getName());
            announcement = announcement
                    .replaceText(config -> {
                        config.match("(?i)\\{player}");
                        config.replacement(player.displayName());
                    })
                    .replaceText(config -> {
                        config.match("(?i)\\{target}");
                        config.replacement(targetName);
                    })
                    .replaceText(config -> {
                        config.match("(?i)\\{rewards}");
                        config.replacement(rewardsTextComponent);
                    });
            StringFormat.msg(player, StringFormat.formatString(this.plugin.getConfig().getString("prefix")).append(announcement));
        }
    }

    @EventHandler
    public void OnSetBountyGuiClose(InventoryCloseEvent event) {
        if (!event.getView().title().equals(StringFormat
                .formatString(plugin.getConfig().getConfigurationSection("lang").getString("setbounty-gui-title"))))
            return;

        Player player = (Player) event.getPlayer();
        if (successfullySetBounty.contains(player))
            return;

        successfullySetBounty.remove(player);
        ItemStack[] items = filterInvItems(event.getInventory().getContents());
        event.getPlayer().getInventory().addItem(items);
    }

    @EventHandler
    public void OnPlayerKill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = event.getEntity().getKiller();
        this.plugin.claimBounty(killed, killer);
    }

    public void openSign(Player target) {
        SignMenuFactory.Menu menu = this.plugin.getSignMenuFactory().newMenu(Arrays.asList("", "^^^^^^^^", "Enter reward", "amount above"))
                .reopenIfFail(false)
                .response((player, strings) -> {
                    String eco = strings[0];

                    Inventory inv = setBountyMenuMap.get(player);
                    setBountyMenuMap.remove(player);

                    ItemStack ecoSet = inv.getItem(2);
                    ItemMeta meta = ecoSet.getItemMeta();
                    if (meta.lore().size() > 1) {
                        List<Component> newLore = meta.lore();
                        newLore.set(0, Component.text(ChatColor.DARK_GREEN + "Current Reward: " + ChatColor.GOLD + plugin.getConfig().getString("currency") + eco));
                        meta.lore(newLore);
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
