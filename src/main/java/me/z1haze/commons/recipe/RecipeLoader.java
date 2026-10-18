package me.z1haze.commons.recipe;

import me.z1haze.commons.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeLoader {
    private static final String REGEX = "\\[([^\\]]*)\\]";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final char EMPTY = ' ';
    private final JavaPlugin plugin;
    private final Map<String, ItemStack> customItems = new HashMap<>();

    public RecipeLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public RecipeLoader registerItem(String key, ItemStack item) {
        customItems.put(key.toLowerCase(Locale.ROOT), item);
        return this;
    }

    /**
     * Registers every enabled recipe under {@code configPath}.
     * A broken entry is logged and skipped so it can't take the other recipes down with it.
     * Returns the keys that were actually added, so callers can remove exactly those later
     * (the config may have changed by then).
     */
    public List<NamespacedKey> loadRecipes(String configPath, String recipeName, ItemStack result) {
        List<NamespacedKey> loaded = new ArrayList<>();

        for (Map<?, ?> recipe : plugin.getConfig().getMapList(configPath)) {
            Object id = recipe.get("id");
            if (id == null) {
                ChatUtils.logColorized("&cSkipped a " + recipeName + " recipe with no id");
                continue;
            }

            if (!Boolean.TRUE.equals(recipe.get("enabled"))) continue;

            try {
                NamespacedKey key = new NamespacedKey(plugin, recipeName + "_" + id.toString().toLowerCase(Locale.ROOT));
                Bukkit.addRecipe(buildRecipe(key, recipe, result));
                loaded.add(key);
            } catch (IllegalArgumentException | IllegalStateException | ClassCastException e) {
                ChatUtils.logColorized("&cSkipped " + recipeName + " recipe '" + id + "': " + e.getMessage());
            }
        }

        ChatUtils.logColorized("&2✓ &rLoaded " + loaded.size() + " " + recipeName + " recipes.");
        return loaded;
    }

    public void unloadRecipes(List<NamespacedKey> keys) {
        keys.forEach(Bukkit::removeRecipe);
    }

    @SuppressWarnings("unchecked")
    private ShapedRecipe buildRecipe(NamespacedKey key, Map<?, ?> recipe, ItemStack result) {
        Object shape = recipe.get("shape");
        Object materials = recipe.get("materials");
        if (!(shape instanceof List<?>)) throw new IllegalArgumentException("missing shape");
        if (!(materials instanceof Map<?, ?>)) throw new IllegalArgumentException("missing materials");

        ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
        shapedRecipe.shape(parseShape((List<String>) shape));

        for (Map.Entry<String, String> entry : ((Map<String, String>) materials).entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new IllegalArgumentException("material symbol '" + entry.getKey() + "' must be one character");
            }

            ItemStack ingredient = resolveItem(entry.getValue());
            shapedRecipe.setIngredient(entry.getKey().charAt(0), new RecipeChoice.ExactChoice(ingredient));
        }

        return shapedRecipe;
    }

    private ItemStack resolveItem(String name) {
        if (name == null) throw new IllegalArgumentException("empty material");

        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.startsWith("{") && lower.endsWith("}")) {
            ItemStack custom = customItems.get(lower);
            if (custom == null) throw new IllegalArgumentException("unknown custom item " + name);
            return custom;
        }

        Material material = Material.matchMaterial(name);
        if (material == null || !material.isItem()) throw new IllegalArgumentException("unknown material " + name);

        return new ItemStack(material);
    }

    /**
     * Turns rows like "[L][][L]" into "L L". Empty cells become spaces, which Bukkit treats as nothing.
     */
    private String[] parseShape(List<String> rows) {
        String[] shape = new String[rows.size()];

        for (int i = 0; i < rows.size(); i++) {
            Matcher matcher = PATTERN.matcher(rows.get(i));
            StringBuilder builder = new StringBuilder();
            while (matcher.find()) {
                builder.append(matcher.group(1).isEmpty() ? EMPTY : matcher.group(1).charAt(0));
            }
            shape[i] = builder.toString();
        }

        return shape;
    }
}
