package co.neweden.LootCrates;

import co.neweden.LootCrates.listeners.PlayerListener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener {
    private static Plugin plugin;

        // When the plugin load/unload
        @Override
        public void onEnable() {
            plugin = this;
            this.getLogger().info("LootCrates is now running");

            saveDefaultConfig();
            registerEvents();
            ConfigRetriever cfr = new ConfigRetriever(this);
            Timer timer = new Timer(this);

            // LoadDb();

        }

        @Override
        public void onDisable() {
            this.getLogger().info("LootCrates has now stopped");
        }
        public static Plugin getPlugin() {
            return plugin;
        }


        private void registerEvents() {
            PluginManager pm = getServer().getPluginManager();

            pm.registerEvents(new PlayerListener(), this);
        }

//SQL query for later
//"CREATE TABLE IF NOT EXISTS `loots` ( `name` VARCHAR(48) NOT NULL , `total_amount` INT UNSIGNED NOT NULL DEFAULT '0' , `one_star` INT UNSIGNED NOT NULL DEFAULT '0' , `two_star` INT UNSIGNED NOT NULL DEFAULT '0' , `three_star` INT UNSIGNED NOT NULL DEFAULT '0' , `four_star` INT UNSIGNED NOT NULL DEFAULT '0' , `five_star` INT UNSIGNED NOT NULL DEFAULT '0' , PRIMARY KEY (`name`(48)))"

}