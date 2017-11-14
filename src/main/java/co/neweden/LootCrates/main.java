package co.neweden.LootCrates;

import co.neweden.LootCrates.listeners.PlayerListener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

import static co.neweden.LootCrates.Database.getConnection;

public class main extends JavaPlugin implements Listener {
    public static Plugin plugin;

    public static Connection con;
        // When the plugin load/unload
        @Override
        public void onEnable() {
            plugin = this;
            this.getLogger().info("LootCrates is now running");

            saveDefaultConfig();
            registerEvents();
            ConfigRetriever cfr = new ConfigRetriever(this);
            Timer timer = new Timer(this);

            try {
                con = getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().info("Database connection failed!! Please verify your MYSQL Config !!");
            }
            Database.initDatabase();


        }

        @Override
        public void onDisable() {

            //Database.deleteChest();//remove all chests from the database and delete them
            Database.removeChests();
            try {
                con.close();
                this.getLogger().info("Database Connection has stopped");
            } catch (SQLException e) {
                //e.printStackTrace();
                this.getLogger().info("Database Connection could not stop!!");
            }
            this.getLogger().info("LootCrates has now stopped");
        }

        private void registerEvents() {
            PluginManager pm = getServer().getPluginManager();

            pm.registerEvents(new PlayerListener(), this);
        }

}