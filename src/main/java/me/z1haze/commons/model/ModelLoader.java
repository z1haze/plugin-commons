package me.z1haze.commons.model;

import me.z1haze.commons.util.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ModelLoader {
    private final FileConfiguration modelData;

    public ModelLoader(JavaPlugin plugin) {
        String fileName = "modelData.yml";
        File file = new File(plugin.getDataFolder(), fileName);

        try {
            InputStream resource = plugin.getResource(fileName);
            if (resource != null && !file.exists()) {
                FileUtils.copyInputStreamToFile(resource, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        modelData = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getModelData() {
        return modelData;
    }
}
