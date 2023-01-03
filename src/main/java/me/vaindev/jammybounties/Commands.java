package me.vaindev.jammybounties;

import me.vaindev.jammybounties.Inventories.BountiesGuis;
import me.vaindev.jammybounties.Inventories.BountySetGui;
import me.vaindev.jammybounties.Utils.StringFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    private final JammyBounties plugin;

    public Commands(JammyBounties plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bounties")) {
            if (args.length == 0) {
                if (!(sender instanceof Player player)) {
                    StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("sender-must-be-player"));
                    return true;
                }

                BountiesGuis gui = new BountiesGuis(plugin, player);
                player.openInventory(gui.getInventory());
                return true;
            }
        }

        if(cmd.getName().equalsIgnoreCase("bountiesreload")) {
            StringFormat.msg(sender, plugin.getConfig().getString("prefix") + " &aReload successful!");
            plugin.reloadConfig();
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("setbounty")) {
            if (!(sender instanceof Player player)) {
                StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("sender-must-be-player"));
                return true;
            }

            if (args.length == 0) {
                StringFormat.msg(player, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command-setbounty"));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                StringFormat.msg(player, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command-setbounty"));
                return true;
            }

            if (DataAccess.getBounties().size() > plugin.getConfig().getInt("bounty-limit")
                    && plugin.getConfig().getInt("bounty-limit") != -1) {
                StringFormat.msg(player, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("bounty-limit-reached"));
                return true;
            }

            BountySetGui gui = new BountySetGui(plugin, target.getUniqueId());
            player.openInventory(gui.getInventory());
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("claimbounty")) {
            if (!(sender instanceof Player player)) {
                StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("sender-must-be-player"));
                return true;
            }

            if (args.length == 0) {
                StringFormat.msg(player, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command-claimbounty"));
                return true;
            }
            Player target = plugin.getServer().getPlayer(args[0]);

            if (target == null) {
                StringFormat.msg(player, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command-claimbounty"));
                return true;
            }

            this.plugin.claimBounty(target, player);
            return true;
        }

        StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command"));
        return true;
    }
}
