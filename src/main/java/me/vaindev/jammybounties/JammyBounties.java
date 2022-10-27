package me.vaindev.jammybounties;

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

        getCommand("bounties").setExecutor(new Commands(this));
        getCommand("setbounty").setExecutor(new Commands(this));
        getCommand("claimbounty").setExecutor(new Commands(this));
        getCommand("bountyreload").setExecutor(new Commands(this));

        getCommand("setbounty").setTabCompleter(new TabComplete());
        getCommand("claimbounty").setTabCompleter(new TabComplete());

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
        String rewardString = bounty.getRewardsAsString(this.getConfig().getString("currency"));
        DataAccess.removeBounty(killedId);

        killer.getInventory().addItem(bounty.getItems());
        economy.depositPlayer(killer, bounty.getEco());
        String announcementString = this.getConfig().getString("prefix") + this.getConfig().getConfigurationSection("lang").getString("bounty-claimed");
        announcementString = announcementString
                .replaceAll("(?i)\\{killer}", killer.getDisplayName())
                .replaceAll("(?i)\\{target}", killed.getDisplayName())
                .replaceAll("(?i)\\{rewards}", rewardString);

        this.getServer().broadcastMessage(announcementString);
    }
}