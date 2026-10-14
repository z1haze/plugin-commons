package me.z1haze.commons.recipe;

import me.z1haze.commons.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeLoader {
    private static final String REGEX = "\\[([^\\]]*)\\]";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private final JavaPlugin plugin;
    private final Map<String, ItemStack> customItems = new HashMap<>();

    public RecipeLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public RecipeLoader registerItem(String key, ItemStack item) {
        customItems.put(key.toLowerCase(), item);
        return this;
    }

    @SuppressWarnings("unchecked")
    public int loadRecipes(String configPath, String recipeName, ItemStack result) {
        List<Map<?, ?>> recipes = plugin.getConfig().getMapList(configPath);
        int count = 0;

        for (Map<?, ?> recipe : recipes) {
            if (!(Boolean) recipe.get("enabled")) continue;

            NamespacedKey key = new NamespacedKey(plugin, recipeName + "_" + recipe.get("id"));
            ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
            String[] shape = parseShape((List<String>) recipe.get("shape"));
            shapedRecipe.shape(shape[0], shape[1], shape[2]);

            Map<String, String> materials = (Map<String, String>) recipe.get("materials");
            for (Map.Entry<String, String> entry : materials.entrySet()) {
                ItemStack ingredient = resolveItem(entry.getValue());
                shapedRecipe.setIngredient(entry.getKey().charAt(0), new RecipeChoice.ExactChoice(ingredient));
            }

            if (Arrays.asList(shape).contains("_")) {
                shapedRecipe.setIngredient('_', new RecipeChoice.MaterialChoice(Material.AIR));
            }

            Bukkit.addRecipe(shapedRecipe);
            count++;
        }

        ChatUtils.logColorized("&2✓ &rLoaded " + count + " " + recipeName + " recipes.");
        return count;
    }

    private ItemStack resolveItem(String name) {
        if (name == null) {
            return new ItemStack(Material.STONE);
        }

        String lower = name.toLowerCase();
        if (lower.startsWith("{") && lower.endsWith("}")) {
            String id = lower.substring(1, lower.length() - 1);
            ItemStack custom = customItems.get("{" + id + "}");
            if (custom != null) {
                return custom;
            }
        }

        return new ItemStack(Material.valueOf(name.toUpperCase()));
    }

    private String[] parseShape(List<String> rows) {
        String[] shape = new String[3];
        int index = 0;

        for (String row : rows) {
            Matcher matcher = PATTERN.matcher(row);
            StringBuilder builder = new StringBuilder();
            while (matcher.find()) {
                String symbol = matcher.group(1).isEmpty() ? "_" : matcher.group(1);
                builder.append(symbol.charAt(0));
            }
            shape[index++] = builder.toString();
        }

        return shape;
    }
}
