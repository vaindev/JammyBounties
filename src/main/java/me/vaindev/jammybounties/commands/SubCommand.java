package me.vaindev.jammybounties.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract String getRequiredPermission();

    public abstract List<String> getArguments(String[] args);

    public abstract boolean run(CommandSender sender, String[] args);
}
