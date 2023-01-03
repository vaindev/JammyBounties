package me.vaindev.jammybounties.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.vaindev.jammybounties.JammyBounties;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormat {

    private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    public static void msg(CommandSender sender, String message) {
        if (message != null && !message.isEmpty()) {
            String formattedMsg = formatString(sender, message);
            sender.sendMessage(formattedMsg);
        }
    }

    public static String formatString(String message) {
        Matcher match = pattern.matcher(message);

        while (match.find()) {
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, ChatColor.of(color) + "");
            match = pattern.matcher(message);
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    public static String formatString(CommandSender sender, String message) {
        Matcher match = pattern.matcher(message);

        if (JammyBounties.PAPI_ENABLED)
            message = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, message);

        while (match.find()) {
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, ChatColor.of(color) + "");
            match = pattern.matcher(message);
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }
}