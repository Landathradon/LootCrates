package co.neweden.LootCrates;

import co.neweden.LootCrates.listeners.PlayerListener;

import co.neweden.LootCrates.listeners.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class Main extends JavaPlugin implements Listener {
    public static Main plugin;
    static Connection con;
    private static boolean Disabled;

    public static Main getPlugin() { return plugin; }

    // When the plugin load/unload
    @Override
    public void onEnable() {
        plugin = this;
        debugActive(true, "LootCrates is now running", null);

        CommandExecutor cmd = new Commands(this);
        this.getCommand("LootCrates").setExecutor(cmd);

        //noinspection unused
        ConfigRetriever.getConfigStuff();
        ConfigRetriever.checkConfig(true);
        if (!Disabled) {
            startup();
        }
    }

    private void startup() {
        saveDefaultConfig();
        registerEvents();

        try {
            con = Database.getConnection();
        } catch (SQLException e) {
            debugActive(true, "Database connection failed!! Please verify your MYSQL Config !!", null);
        }

        Database.initDatabase();
        //Database.loadCrates(); //Check if crates already exists in the db otherwise spawns em - need to be added OnWorldLoad event
        Timer.startDeSpawnTimer();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        ConfigRetriever.checkConfig(false);
        if (!Disabled) {

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
            plugin.getLogger().log(Level.SEVERE, msg, e);
            exception = true;
        }
        if (important && !exception) {
            plugin.getLogger().info(msg);
        } else //noinspection ConstantConditions
            if (ConfigRetriever.Debug && !important && !exception) {
            plugin.getLogger().info(msg);
        }
    }

    static void disablePlugin() {
        Bukkit.getPluginManager().disablePlugin(Main.plugin);
        Disabled = true;
    }
}