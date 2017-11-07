package co.neweden.LootCrates;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import static co.neweden.LootCrates.ChestSpawner.ChestLocArray;
import static co.neweden.LootCrates.ChestSpawner.ArrayIndex;
import static co.neweden.LootCrates.ConfigRetriever.MaxSpawnTime;
import static co.neweden.LootCrates.listeners.PlayerListener.Crates;
import static co.neweden.LootCrates.listeners.PlayerListener.player;

public class Timer{
 private static main plugin;
 public static long TimeSecs = (long)(MaxSpawnTime*60);
 public static long GameTicks = (long)(TimeSecs*20);

    public Timer(main pl) {
        plugin = pl;
    }

    public static void OnCrateCreated(){
        player.sendMessage("The crate is about to despawn in: "+ TimeSecs);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage(ChatColor.BLUE + "DEBUG | OnCrateCreated Function");
            DespawnChest();
        },GameTicks);

    }

    public static void DespawnChest() {
        //get chest locs from db
        player.sendMessage(ChatColor.BLUE + "DEBUG | Top of DespawnChest");
        int x = 0;
        int y = 0;
        int z = 0;

        int i;
        for (i = 0; i < ChestLocArray.length; i++) {
// made progress but im stuck here with this loop and deleting stuff ( Will wait till database is ready)
            ChestLocArray[i][0] = Crates;
            ChestLocArray[i][1] = x;
            ChestLocArray[i][2] = y;
            ChestLocArray[i][3] = z;

            player.sendMessage(ChatColor.BLUE + "DEBUG | Despawning chests from Array");
            Block newBlock = plugin.getServer().getWorlds().get(0).getBlockAt(x, y, z);
            newBlock.setType(Material.AIR);
            //String commandToSend = "setblock ~" + x + " ~" + y + " ~" + z + " air";
            //Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandToSend);
        }
        Crates = Crates - i;
        ArrayIndex = ArrayIndex - i;
        i = 0;

    }

}
