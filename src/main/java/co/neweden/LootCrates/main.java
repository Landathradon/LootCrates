package co.neweden.LootCrates;

import co.neweden.LootCrates.listeners.PlayerListener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class main extends JavaPlugin implements Listener {

    // When the plugin load/unload
    @Override
    public void onEnable() {
        this.getLogger().info("LootCrates is now running");

        createConfig();
        registerEvents();
        ConfigRetriever cfr = new ConfigRetriever(this);

        // LoadDb();

    }

    @Override
    public void onDisable() {
        this.getLogger().info("LootCrates has now stopped");
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerListener(), this);
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

//SQL query for later
//"CREATE TABLE IF NOT EXISTS `loots` ( `name` VARCHAR(48) NOT NULL , `total_amount` INT UNSIGNED NOT NULL DEFAULT '0' , `one_star` INT UNSIGNED NOT NULL DEFAULT '0' , `two_star` INT UNSIGNED NOT NULL DEFAULT '0' , `three_star` INT UNSIGNED NOT NULL DEFAULT '0' , `four_star` INT UNSIGNED NOT NULL DEFAULT '0' , `five_star` INT UNSIGNED NOT NULL DEFAULT '0' , PRIMARY KEY (`name`(48)))"

}