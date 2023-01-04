package me.vaindev.jammybounties;

import me.clip.placeholderapi.libs.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Text;

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

    public Component getRewardsTextComponent(String currencySymbol) {
        TextComponent component = Component.empty();

        if (items != null)
            for (ItemStack item : items) {
                component = component
                        .append(Component.text(
                                ChatColor.YELLOW
                                        + ""
                                        + item.getAmount()
                                        + "x "
                                        + ChatColor.WHITE
                                ))
                        .append(item.displayName())
                        .append(Component.text(", "));
            }

        if (component.content().isEmpty())
            component = component.append(Component.text(ChatColor.GREEN + currencySymbol + eco));
        else
            component = component.append(Component.text("and " + ChatColor.GREEN + currencySymbol + eco));

        return component;
    }
}
