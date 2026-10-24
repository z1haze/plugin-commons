package me.z1haze.commons.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatUtils {
    private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    private static JavaPlugin plugin;
    private static String prefix = "";

    public static void init(JavaPlugin plugin, String prefix) {
        ChatUtils.plugin = plugin;
        ChatUtils.prefix = prefix;
    }

    public static String colorize(String text) {
        String[] parts = text.split(String.format(WITH_DELIMITER, "&"));
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equalsIgnoreCase("&")) {
                i++;
                if (parts[i].charAt(0) == '#') {
                    builder.append(ChatColor.of(parts[i].substring(0, 7))).append(parts[i].substring(7));
                } else {
                    builder.append(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&" + parts[i]));
                }
            } else {
                builder.append(parts[i]);
            }
        }

        return builder.toString();
    }

    public static void logColorized(String message) {
        if (message != null && plugin != null) {
            plugin.getServer().getConsoleSender().sendMessage(colorize(prefix + " " + message));
        }
    }

    public static void sendPrefixed(CommandSender sender, String message) {
        sendColorized(sender, prefix + " " + message);
    }

    public static void sendColorized(CommandSender sender, String message) {
        if (message != null) {
            sender.sendMessage(colorize(message));
        }
    }
}
