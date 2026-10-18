package me.z1haze.commons.model;

import me.z1haze.commons.util.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ModelLoader {
    private final FileConfiguration modelData;

    public ModelLoader(JavaPlugin plugin) {
        modelData = FileUtils.loadWithDefaults(plugin, "modelData.yml");
    }

    public FileConfiguration getModelData() {
        return modelData;
    }
}
