package me.vaindev.jammybounties.commands;

import me.vaindev.jammybounties.JammyBounties;
import me.vaindev.jammybounties.utils.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandClaim extends SubCommand {

    private final JammyBounties plugin;

    public CommandClaim(JammyBounties plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getRequiredPermission() {
        return "bounties.claim";
    }

    @Override
    public List<String> getArguments(String[] args) {
        return null;
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
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
            StringFormat.msg(player, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command-claimbounty"));
            return true;
        }

        this.plugin.claimBounty(target, player);
        return true;
    }
}
