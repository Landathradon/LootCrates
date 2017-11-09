package co.neweden.LootCrates.listeners;

import co.neweden.LootCrates.Chances;

import co.neweden.LootCrates.ChestSpawner;
import co.neweden.LootCrates.Database;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import static co.neweden.LootCrates.ConfigRetriever.FoundChest;
import static co.neweden.LootCrates.ConfigRetriever.MaxCrates;


public class PlayerListener implements Listener {

    public static Player player;
    public static int temp = Math.random() < 0.5 ? 0 : 1;
    public static int Crates = 0;
    ChestSpawner Cs = new ChestSpawner();

    // Remove chest when close
    @EventHandler
    public void onChestClose(InventoryCloseEvent event) {
        if (player!=null ) {
            if (event.getInventory().getHolder() instanceof Chest) {
                Chest c = (Chest) event.getInventory().getHolder();

                //Chest was found by a player | marking it as found
                Database.chestIsFound(player.getDisplayName(),c.getX(),c.getY(),c.getZ());

                ItemStack[] items = event.getInventory().getContents();

                for (ItemStack item : items) {
                    if (item != null) {
                        return;
                    }
                }
                Database.removeChestEvent(c.getX(),c.getY(),c.getZ());
                //Run code to execute if the chest is empty.
                player.sendMessage(FoundChest);
                c.getLocation().getBlock().setType(Material.AIR);
                //Crates = Crates - 1;
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String pname= player.getDisplayName();
        Database.initPlayerChestCount(pname);
    }



    // check if it's a player who is shooting
    @EventHandler
    public void onArrowShoot(EntityShootBowEvent event) {
        LivingEntity user = event.getEntity();

        if (user instanceof Player) {
            player = (Player) user;

            // Checks if we haven't spawned too many Crates
            if (Crates <= MaxCrates) {

                double d = Chances.ChanceCalc();

                // 20% chance Five Star
                if (d <= 0.20) {
                    player.sendMessage(ChatColor.GREEN + "You are so lucky !! : " + (d * 100) + " %");

                    Cs.SpawnChest(5);
                    Crates = Crates + 1;
                }

                // 20% chance Four Star
                else if (d > 0.20 && d <= 0.40) {
                    player.sendMessage(ChatColor.AQUA + "Wow ! : " + (d * 100) + " %");

                    Cs.SpawnChest(4);
                    Crates = Crates + 1;
                }

                // 20% chance Three Star
                else if (d > 0.40 && d <= 0.60){
                    player.sendMessage(ChatColor.GOLD + "Not so bad : " + (d * 100) + " %");

                    Cs.SpawnChest(3);
                    Crates = Crates + 1;
                }
                // 20% chance Two Star
                else if (d > 0.60 && d <= 0.80){
                    player.sendMessage(ChatColor.YELLOW + "Better next time : " + (d * 100) + " %");

                    Cs.SpawnChest(2);
                    Crates = Crates + 1;
                }
                // 20% chance One Star
                else {
                    player.sendMessage(ChatColor.RED + "Meh (╯°□°）╯︵ ┻━┻ : " + (d * 100) + " %");

                    Cs.SpawnChest(1);
                    Crates = Crates + 1;
                }

            }
            // if we have reached MaxCrates set in config it will throw this error
            else{
                player.sendMessage(ChatColor.RED + "You have reached the max amount of crates that can be spawned at once !");
            }

        }
    }
}


