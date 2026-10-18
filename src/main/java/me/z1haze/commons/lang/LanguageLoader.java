package me.z1haze.commons.lang;

import me.z1haze.commons.util.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LanguageLoader {
    private final String[] LANGUAGES = {"en"};
    private final FileConfiguration translations;

    public LanguageLoader(JavaPlugin plugin) {
        String selected = plugin.getConfig().getString("lang");

        // Create or update every bundled language file
        for (String lang : LANGUAGES) {
            if (!lang.equals(selected)) {
                FileUtils.loadWithDefaults(plugin, "lang/" + lang + ".yml");
            }
        }

        translations = FileUtils.loadWithDefaults(plugin, "lang/" + selected + ".yml");
    }

    public FileConfiguration getTranslations() {
        return translations;
    }
}
