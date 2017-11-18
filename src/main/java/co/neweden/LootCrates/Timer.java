package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import static co.neweden.LootCrates.Chances.randomDespawnTime;
import static co.neweden.LootCrates.ChestSpawner.newChest;
import static co.neweden.LootCrates.Database.getChestFromNum;
import static co.neweden.LootCrates.Database.removeChestEvent;
import static co.neweden.LootCrates.main.debugActive;

public class Timer{
 private static main plugin;

    Timer(main pl) {
        plugin = pl;
    }

    static void OnCrateCreated(int num){
        long GameTicks = randomDespawnTime();
        debugActive(false,"The crate is about to despawn in: " + (GameTicks/20)  + " secs");

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                DespawnChest(num, false),GameTicks);

    }
    public static void OnCrateCreated(int num, long timeWanted){
        debugActive(false,"The crate is about to despawn in: " + (timeWanted/20) + " secs");

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                DespawnChest(num,false),timeWanted);

    }

    static void DespawnChest(int num, boolean noRespawn) {
        int[] chest = getChestFromNum(num); //get chest locs from db
        if (chest[7] == 1) {
            World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
            int x = chest[1];
            int y = chest[2];
            int z = chest[3];

            //Despawn Chests
            Location chestLoc = new Location(w, x, y, z);
            Chest ch = (Chest) chestLoc.getBlock().getState();
            Inventory ChestInv = ch.getInventory();
            ChestInv.clear();
            chestLoc.getBlock().setType(Material.AIR);
            removeChestEvent(x, y, z);
            if (!noRespawn) {
                newChest(num);
            }

        }
    }
}
