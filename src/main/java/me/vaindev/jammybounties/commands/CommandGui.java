package me.vaindev.jammybounties.commands;

import me.vaindev.jammybounties.JammyBounties;
import me.vaindev.jammybounties.inventories.BountiesGuis;
import me.vaindev.jammybounties.utils.StringFormat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandGui extends SubCommand {

    private final JammyBounties plugin;

    public CommandGui(JammyBounties plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "gui";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getRequiredPermission() {
        return "bounties.use";
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public List<String> getArguments(String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            StringFormat.msg(sender, plugin.getConfig().getString("prefix") + plugin.getConfig().getConfigurationSection("lang").getString("sender-must-be-player"));
            return true;
        }

        BountiesGuis gui = new BountiesGuis(plugin, player);
        player.openInventory(gui.getInventory());
        return true;
    }
}
