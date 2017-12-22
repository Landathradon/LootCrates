package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

public class Timer{
    private static Main plugin;

    Timer(Main pl) {
        plugin = pl;
    }

    static void OnCrateCreated(Block block){
        long GameTicks = Chances.randomDespawnTime();
        Main.debugActive(false,"The crate is about to despawn in: " + (GameTicks/20)  + " secs", null);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                DespawnChest(block, false),GameTicks);

    }

    public static void OnCrateCreated(Block block, long timeWanted) {
        Main.debugActive(false, "The crate is about to despawn in: " + (timeWanted / 20) + " secs", null);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                DespawnChest(block, false), timeWanted);

    }

    static void DespawnChest(Block block, boolean noRespawn) {
        Database.ChestClass chClass = Database.getCrateFromHashMap(block);
        if (chClass == null) return;

        //Despawn Chests
        Chest ch = (Chest) block.getState();
        ch.getInventory().clear();
        block.getLocation().getBlock().setType(Material.AIR);
        Database.removeChest(block);
        if (!noRespawn) {
            ChestSpawner.newChest();
        }
    }
}
