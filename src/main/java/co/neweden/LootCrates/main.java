package co.neweden.LootCrates;

import co.neweden.LootCrates.listeners.PlayerListener;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;



public class main extends JavaPlugin implements Listener{
    // When the plugin load/unload
    @Override
    public void onEnable() {
        setupPermissions();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getLogger().info("LootBox is now running");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("LootBox has now stopped");
    }

    // setup vault permissions
    public static Permission permission = null;

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }




}