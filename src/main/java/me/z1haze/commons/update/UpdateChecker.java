package me.z1haze.commons.update;

import me.z1haze.commons.util.ChatUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.function.Consumer;

public final class UpdateChecker implements Listener {
    private final VersionSource source;
    private final String pluginName;
    private final String currentVersion;
    private final int resourceId;
    private final String notifyPermission;
    private final Consumer<String> console;
    private volatile String notice;

    UpdateChecker(VersionSource source, String pluginName, String currentVersion, int resourceId,
                  String notifyPermission, Consumer<String> console) {
        this.source = source;
        this.pluginName = pluginName;
        this.currentVersion = currentVersion;
        this.resourceId = resourceId;
        this.notifyPermission = notifyPermission;
        this.console = console;
    }

    public static void start(JavaPlugin plugin, int resourceId, String notifyPermission) {
        UpdateChecker checker = new UpdateChecker(new SpigotVersionSource(resourceId), plugin.getName(),
                plugin.getDescription().getVersion(), resourceId, notifyPermission, plugin.getLogger()::info);

        plugin.getServer().getPluginManager().registerEvents(checker, plugin);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, checker::check);
    }

    // any failure stays silent so an offline server's console doesn't fill with errors
    void check() {
        String latest;
        try {
            latest = source.latest();
        } catch (IOException | RuntimeException e) {
            return;
        }
        if (!Versions.isNewer(latest, currentVersion)) return;

        notice = "A new version of " + pluginName + " is available: " + latest.trim() + " (you have " + currentVersion
                + "). https://www.spigotmc.org/resources/" + resourceId;
        console.accept(notice);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String notice = this.notice;
        if (notice != null && event.getPlayer().hasPermission(notifyPermission)) {
            ChatUtils.sendPrefixed(event.getPlayer(), notice);
        }
    }
}
