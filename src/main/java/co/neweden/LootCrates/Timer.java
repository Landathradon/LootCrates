package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import static co.neweden.LootCrates.Chances.randomDespawnTime;
import static co.neweden.LootCrates.ChestSpawner.newChest;
import static co.neweden.LootCrates.Database.*;
import static co.neweden.LootCrates.Main.debugActive;

public class Timer{
 private static Main plugin;

    Timer(Main pl) {
        plugin = pl;
    }

    static void OnCrateCreated(int num){
        long GameTicks = randomDespawnTime();
        debugActive(false,"The crate is about to despawn in: " + (GameTicks/20)  + " secs", null);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                DespawnChest(num, false),GameTicks);

    }
    public static void OnCrateCreated(int num, long timeWanted){
        debugActive(false,"The crate is about to despawn in: " + (timeWanted/20) + " secs", null);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                DespawnChest(num,false),timeWanted);

    }

    static void DespawnChest(int num, boolean noRespawn) {
        ChestFromNum chFNum = getChestFromNum(num); //get chest locs from db
        if (chFNum.exist == 1) {
            World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);

            //Despawn Chests
            Location chestLoc = new Location(w, chFNum.x, chFNum.y, chFNum.z);
            Chest ch = (Chest) chestLoc.getBlock().getState();
            Inventory ChestInv = ch.getInventory();
            ChestInv.clear();
            chestLoc.getBlock().setType(Material.AIR);
            removeChestEvent(chFNum.x, chFNum.y, chFNum.z);
            if (!noRespawn) {
                newChest(num);
            }

        }
    }
}
