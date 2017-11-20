package co.neweden.LootCrates.listeners;

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

import static co.neweden.LootCrates.ChestSpawner.newChest;
import static co.neweden.LootCrates.ChestSpawner.tierCalc;
import static co.neweden.LootCrates.ConfigRetriever.*;
import static co.neweden.LootCrates.Database.*;
import static org.bukkit.ChatColor.translateAlternateColorCodes;


public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block c = event.getBlock();
        if(c.getType() == Material.CHEST){

            //check if chest exists
            ChestNumberReturn chNum = chestNumberReturn(c.getX(),c.getY(),c.getZ());
            if(chNum.value == 1){
                event.setCancelled(true);
                event.getPlayer().sendMessage(lootcratesPrefix + ChatColor.RED + BreakChest);
            }
        }
    }

    // Remove chest when close
    @EventHandler
    public void onChestClose(InventoryCloseEvent event) {
        LivingEntity user = event.getPlayer();
        boolean isPlayer = (user instanceof Player);
        boolean isChest = (event.getInventory().getHolder() instanceof Chest);



        if (!isPlayer) {return;}
        else {
            Player player = (Player) user;
            if (!isChest) {return;}
            else {
                Chest c = (Chest) event.getInventory().getHolder();

                //check if chest exists
                ChestNumberReturn chNum = chestNumberReturn(c.getX(),c.getY(),c.getZ());
                if(chNum.value == 1){
                    ChestFromNum chFNum = getChestFromNum(chNum.num); //Checks if chest is found | have to be after its added to db so it can return true (1)
                    boolean found = (chFNum.found == 0);
                    ItemStack[] items = event.getInventory().getContents();
                    String tier1 = "";
                    if(chFNum.tier == 1){
                        tier1 = ChatColor.WHITE + " | " + ChatColor.RED + "(╯°□°）╯︵ ┻━┻";
                    }
                    String message = ChatColor.GOLD + player.getDisplayName() + ChatColor.GRAY + " has found a " + ChatColor.YELLOW + tierCalc(chFNum.tier) + ChatColor.GRAY + " Crate" + tier1;// + " in " + player.getWorld().getName();

                    for (ItemStack item : items) {
                        if (item != null) {
                            //Checks if chest has already been found so it wont display a message twice
                            if(found) {
                                //Chest was found by a player | marking it as found
                                chestIsFound(player.getUniqueId(),c.getX(),c.getY(),c.getZ());
                                Bukkit.broadcastMessage(message);
                                String FoundChest_NB_Colored = translateAlternateColorCodes('&', FoundChest_NB);
                                player.sendMessage(FoundChest_NB_Colored);
                                Timer.OnCrateCreated(chNum.num, 6000); //6000=5min, 600=30sec
                            }
                            return;
                        }
                    }
                    //Run code to execute if the chest is empty

                    //Checks if chest has already been found so it wont display a message twice
                    if(found) {
                        //Chest was found by a player | marking it as found
                        chestIsFound(player.getUniqueId(),c.getX(),c.getY(),c.getZ());
                        Bukkit.broadcastMessage(message);
                        String FoundChest_Colored = translateAlternateColorCodes('&', FoundChest);
                        player.sendMessage(FoundChest_Colored);
                    }
                    removeChestEvent(c.getX(),c.getY(),c.getZ());
                    c.getLocation().getBlock().setType(Material.AIR);
                    newChest(chFNum.num);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID p_uuid = player.getUniqueId();
        Database.initPlayerChestCount(p_uuid);
    }

}


