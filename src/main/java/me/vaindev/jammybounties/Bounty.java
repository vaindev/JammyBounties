package me.vaindev.jammybounties;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

public record Bounty(UUID uuid, ItemStack[] items, double eco,
                     Date dateCreated) {

    public UUID getUuid() {
        return this.uuid;
    }

    public ItemStack[] getItems() {
        return this.items;
    }

    public double getEco() {
        return this.eco;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public String getRewardsAsString(String currencySymbol) {
        StringBuilder stringBuilder = new StringBuilder();
        if (items != null)
            for (ItemStack item : items) {
                stringBuilder.append(ChatColor.YELLOW + String.valueOf(item.getAmount()) + "x " + ChatColor.WHITE + (item.hasItemMeta() ? item.getItemMeta().getDisplayName() + ", " : item.getType().data.getSimpleName() + ", "));
            }

        if (stringBuilder.isEmpty())
            stringBuilder.append(ChatColor.GREEN + currencySymbol + eco);
        else
        stringBuilder.append("and " + ChatColor.GREEN + currencySymbol + eco);

        return stringBuilder.toString();
    }
}
