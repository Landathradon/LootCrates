package co.neweden.LootCrates.listeners;

import co.neweden.LootCrates.Chances;

import co.neweden.LootCrates.ChestSpawner;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static co.neweden.LootCrates.ConfigRetriever.MaxCrates;
import static co.neweden.LootCrates.Timer.Time;


public class PlayerListener implements Listener {

    public static Player player;
    public static int temp = Math.random() < 0.5 ? 0 : 1;
    public static int Crates = 0;
    ChestSpawner Cs = new ChestSpawner();

    // Remove chest when close
    @EventHandler
    public void onChestClose(InventoryCloseEvent event){
        if(event.getInventory().getHolder() instanceof Chest){
            Chest c = (Chest)event.getInventory().getHolder();
                    ItemStack[] items = event.getInventory().getContents();

                    for(ItemStack item : items)
                    {
                        if(item != null) { return; }
                    }
                    //Run code to execute if the chest is empty.
                    c.getLocation().getBlock().setType(Material.AIR);
                    Crates = Crates - 1;
        }
    }

    // check if it's a player who is shooting
    @EventHandler
    public void onArrowShoot(EntityShootBowEvent event) {
        LivingEntity user = event.getEntity();


        if (user instanceof Player) {
            player = (Player) user;

            // Checks if we haven't spawned too many Crates
            if (Crates < MaxCrates) {

                double d = Chances.ChanceCalc();

                // 5% chance of being here
                if (d < 0.05) {
                    player.sendMessage(ChatColor.GOLD + "You are so lucky !! : " + (d * 100) + " %");

                    Cs.SpawnChest(3);
                    Crates = Crates + 1;
                }

                // 20% chance of being here
                else if (d > 0.05 && d < 0.25) {
                    player.sendMessage(ChatColor.GREEN + "Wow ! : " + (d * 100) + " %");

                    Cs.SpawnChest(2);
                    Crates = Crates + 1;
                }

                // 75% chance of being here
                else {
                    player.sendMessage(ChatColor.RED + "Better next time : " + (d * 100) + " %");

                    Cs.SpawnChest(1);
                    Crates = Crates + 1;
                }

            }
            // if we have reached MaxCrates set in config it will throw this error
            else{
                player.sendMessage(ChatColor.RED + "You have reached the max amount of crates that can be spawned at once !");
                player.sendMessage(ChatColor.RED + "Wait " + Time + " seconds before they despawn");
            }

        }
    }
}


