package co.neweden.LootCrates;

import co.neweden.LootCrates.listeners.PlayerListener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;



public class main extends JavaPlugin implements Listener{
    // When the plugin load/unload
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getLogger().info("LootBox is now running");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("LootBox has now stopped");
    }


}