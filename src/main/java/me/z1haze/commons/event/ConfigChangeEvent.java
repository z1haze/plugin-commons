package me.z1haze.commons.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigChangeEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();
    public final JavaPlugin plugin;

    public ConfigChangeEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
