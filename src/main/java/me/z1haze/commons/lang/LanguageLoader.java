package me.z1haze.commons.lang;

import me.z1haze.commons.util.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LanguageLoader {
    private final String[] LANGUAGES = {"en"};
    private final FileConfiguration translations;

    public LanguageLoader(JavaPlugin plugin) {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.isDirectory()) {
            langDir.mkdir();
        }

        for (String lang : LANGUAGES) {
            File file = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
            try {
                InputStream resource = plugin.getResource("lang/" + lang + ".yml");
                if (resource != null && !file.exists()) {
                    FileUtils.copyInputStreamToFile(resource, file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        translations = YamlConfiguration.loadConfiguration(
                new File(plugin.getDataFolder(), "lang/" + plugin.getConfig().getString("lang") + ".yml"));
    }

    public FileConfiguration getTranslations() {
        return translations;
    }
}
