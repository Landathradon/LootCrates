package co.neweden.LootCrates.listeners;

import co.neweden.LootCrates.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerListener implements Listener{

    public ArrayList<Material> common = new ArrayList<Material>(){{
        add(Material.LOG);
        add(Material.WOOD_AXE);
        //add(Material.BROWN_SHULKER_BOX);
    }};
    public ArrayList<Material> uncommon = new ArrayList<Material>(){{
        add(Material.IRON_ORE);
        add(Material.IRON_AXE);
        //add(Material.SILVER_SHULKER_BOX);
    }};
    public ArrayList<Material> rare = new ArrayList<Material>(){{
        add(Material.DIAMOND);
        add(Material.DIAMOND_AXE);
        //add(Material.YELLOW_SHULKER_BOX);
    }};
//

    public double ChanceCalc(){

        double d = Math.random();
        d = Math.round(d*100.0)/100.0;

        return d;
    }


    // check if it's a player who is shooting
    @EventHandler
    public void onArrowShoot(EntityShootBowEvent event){
        LivingEntity shooter = event.getEntity();

        if(shooter instanceof Player){
            Player player = (Player) shooter;
            Inventory inv = player.getInventory();

            //check if player has permission
            if(main.permission.has(player, "explosivearrows.shoot")) {


                double d = ChanceCalc();

                // 5% chance of being here
                if(d < 0.05){
                    int temp = Math.random() < 0.5 ? 0 : 1;
                    player.sendMessage(ChatColor.GOLD + "You are so lucky !! : " + (d*100) + " %");
                    player.sendMessage("An item was added to your inventory");

                    ItemStack itemToAdd = new ItemStack(rare.get(temp), 1);
                    inv.addItem(itemToAdd);


                }

                // 20% chance of being here
                else if(d > 0.05 && d < 0.25){
                    int temp = Math.random() < 0.5 ? 0 : 1;
                    player.sendMessage(ChatColor.GREEN + "Niceeee ! : " + (d*100) + " %");
                    player.sendMessage("An item was added to your inventory");

                    ItemStack itemToAdd = new ItemStack(uncommon.get(temp), 1);
                    inv.addItem(itemToAdd);

                }

                // 75% chance of being here
                else{
                    int temp = Math.random() < 0.5 ? 0 : 1;
                    player.sendMessage(ChatColor.RED + "Lucky next time : " + (d*100) + " %");
                    player.sendMessage("An item was added to your inventory");

                    ItemStack itemToAdd = new ItemStack(common.get(temp), 1);
                    inv.addItem(itemToAdd);
                    //  String commandToSend = "setblock ~1 ~ ~ brown_shulker_box 0 replace {Items:[{id:\"stone\",Damage:6,Slot:0,Count:11},{id:\"wool\",Damage:11,Slot:2,Count:5},{id:\"gold_ore\",Slot:6,Count:33}],display:{Name:\"Best Chest around\",Lore:[\"mhm woah\",\"Lucky enough\"]}}";
                    //  Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandToSend);

                }
            }
        }
    }
}
