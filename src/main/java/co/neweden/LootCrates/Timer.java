package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import static co.neweden.LootCrates.Chances.randomDespawnTime;
import static co.neweden.LootCrates.ChestSpawner.newChest;
import static co.neweden.LootCrates.main.debugActive;

public class Timer{
 private static main plugin;

    Timer(main pl) {
        plugin = pl;
    }

    static void OnCrateCreated(int num, boolean Empty){
        long GameTicks = randomDespawnTime();
        debugActive(false,"The crate is about to despawn in: " + (GameTicks/20)  + " secs");

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                DespawnChest(num, Empty, false),GameTicks);

    }
    public static void OnCrateCreated(int num, boolean Empty,long timeWanted){
        debugActive(false,"The crate is about to despawn in: " + (timeWanted/20) + " secs");

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                DespawnChest(num, Empty, false),timeWanted);

    }

    static void DespawnChest(int num, boolean Empty, boolean noRespawn) {
        int[] chest = Database.getChestFromNum(num); //get chest locs from db
        if (chest[7] == 1) {
            World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
            int x = chest[1];
            int y = chest[2];
            int z = chest[3];
            int found = chest[5];

            if (found == 0) {
                //Normal Despawn
                Location chestLoc = new Location(w, x, y, z);
                Chest ch = (Chest) chestLoc.getBlock().getState();
                Inventory ChestInv = ch.getInventory();
                ChestInv.clear();
                chestLoc.getBlock().setType(Material.AIR);
                Database.removeChestEvent(x, y, z);
                if(!noRespawn) {
                    newChest(num);
                }

            } else if (found == 1 && Empty) {
                //if chest has been opened and items were still inside
                Location chestLoc = new Location(w, x, y, z);
                Chest ch = (Chest) chestLoc.getBlock().getState();
                Inventory ChestInv = ch.getInventory();
                ChestInv.clear();
                chestLoc.getBlock().setType(Material.AIR);
                Database.removeChestEvent(x, y, z);
                if(!noRespawn) {
                    newChest(num);
                }
            }
        }
    }

}
