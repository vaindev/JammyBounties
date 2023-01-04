package me.vaindev.jammybounties.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.vaindev.jammybounties.JammyBounties;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class StringFormat {

    public static void msg(CommandSender sender, String message) {
        if (message != null && !message.isEmpty()) {
            Component formattedMsg = formatPapi(sender, message);
            sender.sendMessage(formattedMsg);
        }
    }

    public static void msg(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    public static void broadcast(Server server, String message) {
        if (message != null && !message.isEmpty()) {
            Component formattedMsg = formatString(message);
            server.broadcast(formattedMsg);
        }
    }

    public static void broadcast(Server server, Component message) {
        server.broadcast(message);
    }

    public static Component formatString(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public static Component formatPapi(CommandSender sender, String message) {
        if (JammyBounties.PAPI_ENABLED)
            message = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, message);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}