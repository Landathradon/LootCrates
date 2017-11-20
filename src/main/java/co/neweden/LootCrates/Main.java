package co.neweden.LootCrates;

import co.neweden.LootCrates.listeners.PlayerListener;

import co.neweden.LootCrates.listeners.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

import static co.neweden.LootCrates.ChestSpawner.*;
import static co.neweden.LootCrates.ConfigRetriever.*;
import static co.neweden.LootCrates.Database.*;

public class Main extends JavaPlugin implements Listener {
    private static Plugin plugin;
    static Connection con;
    private static boolean Disabled;

    // When the plugin load/unload
    @Override
    public void onEnable() {
        plugin = this;
        debugActive(true, "LootCrates is now running", null);

        CommandExecutor cmd = new Commands(this);
        this.getCommand("LootCrates").setExecutor(cmd);

        //noinspection unused
        ConfigRetriever cfr = new ConfigRetriever(this);
        checkConfig(1);
        if (!Disabled) {
            startup();
        }
    }

    private void startup(){
        saveDefaultConfig();
        registerEvents();
        Timer timer = new Timer(this);
        Commands commands = new Commands(this);

            try {
                con = getConnection();
            } catch (SQLException e) {
                debugActive(true, "Database connection failed!! Please verify your MYSQL Config !!", null);
            }

            initDatabase();
            CreateChestOnStartup(); //Spawns chest when server starts
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelAllTasks();
        checkConfig(0);
        if (!Disabled) {
            count = 1;
            deleteChest();//remove all chests from the database and delete them
            debugActive(true, (count-1) + " Crates have been deleted", null);
            removeChestsFromDb();//for now it will delete everything | Might be useless after few modifications

            try {
                con.close();
                debugActive(true, "Database Connection has stopped", null);
            } catch (SQLException e) {
                debugActive(true, "Database Connection could not stop!!",e);
            }
        }
        debugActive(true, "LootCrates has now stopped", null);
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
    }

    //Checks if Debug is active in the config
    static void debugActive(Boolean important, String msg, Exception e) {
        boolean exception = false;
        if (e != null) {
            plugin.getLogger().info(msg);
            e.printStackTrace();
            exception = true;
        }
        if (important && !exception) {
            plugin.getLogger().info(msg);
        } else //noinspection ConstantConditions
            if (Debug && !important && !exception) {
            plugin.getLogger().info(msg);
        }
    }

    static void disablePlugin() {
        Bukkit.getPluginManager().disablePlugin(Main.plugin);
        Disabled = true;
    }
}