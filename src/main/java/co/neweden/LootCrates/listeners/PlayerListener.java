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
import org.bukkit.inventory.ItemStack;

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
        if (!(event.getPlayer() instanceof Player) || !(event.getInventory().getHolder() instanceof Chest)) return;
        Player player = (Player) event.getPlayer();
        Chest c = (Chest) event.getInventory().getHolder();

        //check if chest exists
        Database.ChestClass chest = Database.getCrateFromHashMap(c.getBlock());
        if (chest == null) return;

        // We need to know if the chest is actually empty or not
        int itemsLeft = 0;
        for (ItemStack item : event.getInventory().getContents()) {
            if (item != null) itemsLeft++;
        }

        if (itemsLeft > 0 && !chest.found) {
            // Chest was found but still has items
            String FoundChest_NB_Colored = ChatColor.translateAlternateColorCodes('&', ConfigRetriever.FoundChest_NB);
            player.sendMessage(FoundChest_NB_Colored);
            Timer.despawnCountdown(c.getBlock(), 6000); //6000=5min, 600=30sec
        }

        if (itemsLeft <= 0) {
            // All items were removed from the chest
            if (!chest.found) { // Chest was found
                String FoundChest_Colored = ChatColor.translateAlternateColorCodes('&', ConfigRetriever.FoundChest);
                player.sendMessage(FoundChest_Colored);
            }
            Database.removeChest(c.getBlock());
            c.getLocation().getBlock().setType(Material.AIR);
            ChestSpawner.newChest();
        }

        if (chest.found) return;

        // Construct broadcast message
        String specialText = "";
        if (chest.tier == 1) {
            specialText = ChatColor.WHITE + " | " + ChatColor.RED + "(╯°□°）╯︵ ┻━┻";
        }
        String message = ChatColor.GOLD + player.getDisplayName() + ChatColor.GRAY + " has found a " + ChatColor.YELLOW + ChestSpawner.tierCalc(chest.tier) + ChatColor.GRAY + " Crate" + specialText;// + " in " + player.getWorld().getName();
        Bukkit.broadcastMessage(message);

        // mark chest as found and update player statistics
        Database.chestIsFound(player,chest);
    }

}


