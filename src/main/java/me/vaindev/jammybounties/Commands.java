package me.vaindev.jammybounties;

import me.vaindev.jammybounties.utils.GuiInitializer;
import me.vaindev.jammybounties.utils.StringFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {

    private final Plugin plugin;
    private final JammyBounties jammyBounties;
    private final GuiInitializer guiInitializer;

    public Commands(Plugin plugin, JammyBounties jammyBounties, GuiInitializer guiInitializer) {
        this.plugin = plugin;
        this.jammyBounties = jammyBounties;
        this.guiInitializer = guiInitializer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bounties")) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("sender-must-be-player"));
                    return true;
                }
                Player player = (Player) sender;

                player.openInventory(guiInitializer.listBountiesGui(1));
                return true;
            }
        }

        if(cmd.getName().equalsIgnoreCase("bountiesreload")) {
            StringFormat.msg(sender, plugin.getConfig().getString("prefix") + " &aReload successful!");
            plugin.reloadConfig();
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("setbounty")) {
            if (!(sender instanceof Player)) {
                StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("sender-must-be-player"));
                return true;
            }
            Player player = (Player) sender;

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

            player.openInventory(guiInitializer.setBountyGui(target.getUniqueId()));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("claimbounty")) {
            if (!(sender instanceof Player)) {
                StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("sender-must-be-player"));
                return true;
            }
            Player player = (Player) sender;

            if (args.length == 0) {
                StringFormat.msg(player, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command-claimbounty"));
                return true;
            }
            Player target = plugin.getServer().getPlayer(args[0]);

            if (target == null) {
                StringFormat.msg(player, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command-claimbounty"));
                return true;
            }

            this.jammyBounties.claimBounty(plugin.getServer().getPlayer(args[0]), player);
            return true;
        }

        StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command"));
        return true;
    }
}
