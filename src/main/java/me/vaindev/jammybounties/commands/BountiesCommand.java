package me.vaindev.jammybounties.commands;

import me.vaindev.jammybounties.JammyBounties;
import me.vaindev.jammybounties.utils.StringFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BountiesCommand implements TabExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>();

    private final JammyBounties plugin;
    public BountiesCommand(JammyBounties plugin) {
        this.plugin = plugin;

        this.subCommands.add(new CommandReload(plugin));
        this.subCommands.add(new CommandClaim(plugin));
        this.subCommands.add(new CommandGui(plugin));
        this.subCommands.add(new CommandSet(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length < 1) {
            new CommandGui(this.plugin).run(sender, args);
            return true;
        }

        for (SubCommand subCommand: getSubCommands())
            if (args[0].equalsIgnoreCase(subCommand.getName())) {
                if (sender.hasPermission(subCommand.getRequiredPermission())) {
                    if (subCommand.run(sender, Arrays.copyOfRange(args, 1, args.length)))
                        return true;
                } else {
                    StringFormat.msg(sender, plugin.getConfig().getConfigurationSection("lang").getString("insufficient-permissions"));
                    return true;
                }
            }

        StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("invalid-command"));
        return true;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            Set<String> subCommandNames = new HashSet<>();

            getSubCommands().forEach(command -> {
                if (sender.hasPermission(command.getRequiredPermission()))
                    subCommandNames.add(command.getName());
            });

            options.addAll(StringUtil.copyPartialMatches(args[0], subCommandNames, new ArrayList<>()));
        }

        if (args.length == 2)
            for (SubCommand subCommand : getSubCommands())
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    if (subCommand.getArguments(args) == null)
                        options = null;
                    else
                        options.addAll(StringUtil.copyPartialMatches(args[1], subCommand.getArguments(args), new ArrayList<>()));
                }

        return options;
    }
}
