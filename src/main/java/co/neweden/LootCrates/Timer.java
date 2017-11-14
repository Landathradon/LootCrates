package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import static co.neweden.LootCrates.ConfigRetriever.MaxSpawnTime;
import static co.neweden.LootCrates.listeners.PlayerListener.player;

public class Timer{
 private static main plugin;
 public static long TimeSecs = (long)(MaxSpawnTime*60);
 public static long GameTicks = (long)(TimeSecs*20);

    public Timer(main pl) {
        plugin = pl;
    }

    public static void OnCrateCreated(int num){
        player.sendMessage("The crate is about to despawn in: "+ TimeSecs);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            DespawnChest(num);
        },GameTicks);

    }

    public static void DespawnChest(int num) {
        int[] chest = Database.getChestFromNum(num); //get chest locs from db
        int x = chest[1];
        int y = chest[2];
        int z = chest[3];
        int found = chest[5];

        if(found == 0) {

            World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
            Location chestLoc = new Location(w, x, y, z);
            Chest ch = (Chest) chestLoc.getBlock().getState();
            Inventory ChestInv = ch.getInventory();
            ChestInv.clear();

            chestLoc.getBlock().setType(Material.AIR);
            Database.removeChestEvent(x, y, z);

        }
    }

}
