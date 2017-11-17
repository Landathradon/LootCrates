package co.neweden.LootCrates;

import co.neweden.LootCrates.listeners.PlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

import static co.neweden.LootCrates.ChestSpawner.CreateChestOnStartup;
import static co.neweden.LootCrates.ConfigRetriever.*;
import static co.neweden.LootCrates.Database.*;

public class main extends JavaPlugin implements Listener {
        private static Plugin plugin;
        static Connection con;
        private static boolean Disabled;

        // When the plugin load/unload
        @Override
        public void onEnable() {
            plugin = this;
            debugActive(true, "LootCrates is now running");

            saveDefaultConfig();
            registerEvents();
            ConfigRetriever cfr = new ConfigRetriever(this);
            Timer timer = new Timer(this);

            try {
                con = getConnection();
            } catch (SQLException e) {
                debugActive(true, "Database connection failed!! Please verify your MYSQL Config !!");
            }

            initDatabase();
            checkMinAndMaxTimes();
            if(!Disabled) {
                CreateChestOnStartup(); //Spawns chest when server starts
            }
        }

        @Override
        public void onDisable() {
            Bukkit.getScheduler().cancelAllTasks();

            deleteChest();//remove all chests from the database and delete them
            removeChestsFromDb();//for now it will delete everything

            try {
                con.close();
                debugActive(true, "Database Connection has stopped");
            } catch (SQLException e) {
                debugActive(true, "Database Connection could not stop!!");
            }
            debugActive(true, "LootCrates has now stopped");

        }

        private void registerEvents() {
            PluginManager pm = getServer().getPluginManager();

            pm.registerEvents(new PlayerListener(), this);
        }

        //Checks if Debug is active in the config
        static void debugActive(Boolean important, String msg){
            if(important){

                plugin.getLogger().info(msg);

            }
            else if(Debug && !important){

                plugin.getLogger().info(msg);

            }
        }

    static void disablePlugin(){

            Bukkit.getPluginManager().disablePlugin(main.plugin);
            Disabled = true;
        }
}