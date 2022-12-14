package me.vaindev.jammybounties;

import me.vaindev.jammybounties.commands.BountiesCommand;
import me.vaindev.jammybounties.utils.StringFormat;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public final class JammyBounties extends JavaPlugin {

    public static final int INV_ITEM_DATA = 2718;
    public static boolean PAPI_ENABLED = false;

    public static Economy economy;

    public SignMenuFactory signMenuFactory;

    public Logger log;

    public SignMenuFactory getSignMenuFactory() {
        return this.signMenuFactory;
    }

    private void initPapi() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            PAPI_ENABLED = true;
    }

    private void initEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        economy = economyProvider.getProvider();
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();
        reloadConfig();
        this.log = getLogger();

        this.signMenuFactory = new SignMenuFactory(this);

        try {
            DataAccess.initDb();
        }
        catch (Exception e) {
            this.log.warning("Something went wrong when initialising database. Exception:\n" + e);
            getPluginLoader().disablePlugin(getPlugin(getClass()));
            return;
        }

        getCommand("bounties").setExecutor(new BountiesCommand(this));

        getServer().getPluginManager().registerEvents(new Events(this), this);

        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[JammyBounties] Plugin is enabled!");

        initEconomy();
        initPapi();
    }

    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[JammyBounties] Plugin is disabled!");
        DataAccess.disconnect();
    }

    public void claimBounty(Player killed, Player killer) {
        UUID killedId = killed.getUniqueId();

        if (DataAccess.getBounty(killedId) == null)
            return;

        Bounty bounty = DataAccess.getBounty(killedId);
        Component rewardsTextComponent = bounty.getRewardsTextComponent(this.getConfig().getString("currency"));
        DataAccess.removeBounty(killedId);

        killer.getInventory().addItem(bounty.getItems());
        economy.depositPlayer(killer, bounty.getEco());
        Component announcement = StringFormat.formatString(this.getConfig().getConfigurationSection("lang").getString("bounty-claimed"));
        announcement = announcement
                .replaceText(config -> {
                    config.match("(?i)\\{killer}");
                    config.replacement(killer.displayName());
                })
                .replaceText(config -> {
                    config.match("(?i)\\{target}");
                    config.replacement(killed.displayName());
                })
                .replaceText(config -> {
                    config.match("(?i)\\{rewards}");
                    config.replacement(rewardsTextComponent);
                });

        StringFormat.broadcast(this.getServer(), StringFormat.formatString(this.getConfig().getString("prefix")).append(announcement));
    }
}