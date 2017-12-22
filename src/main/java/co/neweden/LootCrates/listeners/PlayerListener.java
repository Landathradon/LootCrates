package co.neweden.LootCrates.listeners;

import co.neweden.LootCrates.ChestSpawner;
import co.neweden.LootCrates.ConfigRetriever;
import co.neweden.LootCrates.Database;
import co.neweden.LootCrates.Timer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;



public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block c = event.getBlock();
        if(c.getType() == Material.CHEST){

            //check if chest exists
            Database.ChestClass chClass = Database.getCrateFromHashMap(c);
            if(chClass == null){return;}
            event.setCancelled(true);
            event.getPlayer().sendMessage(ConfigRetriever.lootcratesPrefix + ChatColor.RED + ConfigRetriever.BreakChest);
        }
    }

    // Remove chest when close
    @EventHandler
    public void onChestClose(InventoryCloseEvent event) {
        LivingEntity user = event.getPlayer();
        boolean isPlayer = (user instanceof Player);
        boolean isChest = (event.getInventory().getHolder() instanceof Chest);

        if (!isPlayer) {return;}
        Player player = (Player) user;
        if (!isChest) {return;}
        Chest c = (Chest) event.getInventory().getHolder();

        //check if chest exists
        Database.ChestClass chClass = Database.getCrateFromHashMap(c.getBlock()); //Checks if chest is found
        if(chClass == null || !chClass.found) return;
        boolean found = chClass.found;

        String tier1 = "";
        if (chClass.tier == 1) {
            tier1 = ChatColor.WHITE + " | " + ChatColor.RED + "(╯°□°）╯︵ ┻━┻";
        }
        String message = ChatColor.GOLD + player.getDisplayName() + ChatColor.GRAY + " has found a " + ChatColor.YELLOW + ChestSpawner.tierCalc(chClass.tier) + ChatColor.GRAY + " Crate" + tier1;// + " in " + player.getWorld().getName();

        ItemStack[] items = event.getInventory().getContents();
        for (ItemStack item : items) {
            if (item == null) {continue;}
            //Checks if chest has already been found so it wont display a message twice
            if (found) {return;}
            //Chest was found by a player | marking it as found
            Database.chestIsFound(player.getUniqueId(),c.getBlock());
            Bukkit.broadcastMessage(message);
            String FoundChest_NB_Colored = ChatColor.translateAlternateColorCodes('&', ConfigRetriever.FoundChest_NB);
            player.sendMessage(FoundChest_NB_Colored);
            Timer.OnCrateCreated(c.getBlock(), 6000); //6000=5min, 600=30sec
            return;
        }

        //Run code to execute if the chest is empty
        if(!found){
            Bukkit.broadcastMessage(message);
            String FoundChest_Colored = ChatColor.translateAlternateColorCodes('&', ConfigRetriever.FoundChest);
            player.sendMessage(FoundChest_Colored);
        }
        Database.removeChest(c.getBlock());
        c.getLocation().getBlock().setType(Material.AIR);
        ChestSpawner.newChest();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID p_uuid = player.getUniqueId();
        Database.initPlayerChestCount(p_uuid);//add this to onChestClose
    }
}


