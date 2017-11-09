package co.neweden.LootCrates;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import static co.neweden.LootCrates.ConfigRetriever.MaxSpawnTime;
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
            //DespawnChest();
        },GameTicks);

    }

    public static void DespawnChest() {
        //Database.retrieveChest() //get chest locs from db
        int x = 0;
        int y = 0;
        int z = 0;

            //for loop for each chests
            player.sendMessage(ChatColor.BLUE + "DEBUG | Despawning chests from Array");
            Block newBlock = plugin.getServer().getWorlds().get(0).getBlockAt(x, y, z);
            newBlock.setType(Material.AIR);
            //String commandToSend = "setblock ~" + x + " ~" + y + " ~" + z + " air";
            //Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandToSend);
            //Database.removeChestEvent(x, y, z);


    }

}
