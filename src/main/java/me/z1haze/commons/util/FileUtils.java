package me.z1haze.commons.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static void copyInputStreamToFile(InputStream input, File file) throws IOException {
        try (FileOutputStream output = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        }
    }

    /**
     * Loads a yml file from the plugin's data folder, creating it from the bundled resource if missing.
     * Keys present in the bundled resource but missing from the user's file are written into it;
     * values the user has set are never overwritten. If the user's file fails to parse, it is left
     * untouched and the bundled defaults are used in memory.
     *
     * @param plugin the plugin owning the file
     * @param path   the path relative to the data folder, matching the resource path in the jar
     * @return the loaded configuration, with the bundled resource as its defaults
     */
    public static FileConfiguration loadWithDefaults(JavaPlugin plugin, String path) {
        File file = new File(plugin.getDataFolder(), path);
        YamlConfiguration defaults = loadResource(plugin, path);

        if (!file.exists()) {
            if (defaults == null) return new YamlConfiguration();
            plugin.saveResource(path, false);
        }

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Could not parse " + path + ", using defaults until it is fixed: " + e.getMessage());
            return defaults != null ? defaults : config;
        }

        if (defaults == null) return config;

        config.setDefaults(defaults);

        boolean missingKeys = false;
        for (String key : defaults.getKeys(true)) {
            if (!config.isSet(key)) {
                missingKeys = true;
                break;
            }
        }

        if (missingKeys) {
            config.options().copyDefaults(true);
            try {
                config.save(file);
                plugin.getLogger().info("Added new default values to " + path);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not update " + path + ": " + e.getMessage());
            }
        }

        return config;
    }

    /**
     * Replacement for {@link JavaPlugin#saveDefaultConfig()} that also merges new keys from the
     * bundled config.yml into the user's existing config, then reloads it.
     *
     * @param plugin the plugin owning the config
     */
    public static void saveAndMergeDefaultConfig(JavaPlugin plugin) {
        loadWithDefaults(plugin, "config.yml");
        plugin.reloadConfig();
    }

    private static YamlConfiguration loadResource(JavaPlugin plugin, String path) {
        InputStream resource = plugin.getResource(path);
        if (resource == null) return null;

        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not read bundled " + path + ": " + e.getMessage());
            return null;
        }
    }
}
