package me.vaindev.jammybounties.commands;

import me.vaindev.jammybounties.JammyBounties;
import me.vaindev.jammybounties.utils.StringFormat;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandReload extends SubCommand {

    private final JammyBounties plugin;

    public CommandReload(JammyBounties plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
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
        return "bounties.admin";
    }

    @Override
    public List<String> getArguments(String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        StringFormat.msg(sender, plugin.getConfig().getConfigurationSection("lang").getString("reload"));
        this.plugin.reloadConfig();
        return true;
    }
}
