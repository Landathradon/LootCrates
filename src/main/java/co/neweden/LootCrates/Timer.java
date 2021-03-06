package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import java.util.*;

public class Timer {

    private static Random random = new Random();

    static void startDeSpawnTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Block block = getRandomChestToDespawn();
                if (block == null) return;
                DespawnChest(block, false);
            }
        }, 0L, ConfigRetriever.RespawnFrequency);
    }

    private static Block getRandomChestToDespawn() {
        for (int i = 0; i < 50; i++) {
            Object[] blocks = Database.cratesMap.keySet().toArray();
            if(blocks.length <= 0) return null;
            int index = random.nextInt(blocks.length);
            if(index <= 0) return null;
            Block block = (Block) blocks[index];
            if (!Database.getCrate(block).found)
                return block;
        }
        return null;
    }

    public static void despawnCountdown(Block block, long ticks) {
        Main.debugActive(false, "The crate is about to despawn in: " + (ticks / 20) + " secs", null);

        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () ->
                DespawnChest(block, false), ticks);

    }

    static void DespawnChest(Block block, boolean noRespawn) {
        Database.ChestClass chClass = Database.getCrate(block);
        if (chClass == null) return;

        //Despawn Chests
        Chest ch = (Chest) block.getState();
        ch.getInventory().clear();
        ChestSpawner.RemoveNameTagOverlay(block);
        block.getLocation().getBlock().setType(Material.AIR);
        if (!noRespawn) {
            ChestSpawner.newChest(Bukkit.getWorld(chClass.world));
        }
        Database.removeChest(block);
    }
}
