package co.neweden.LootCrates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static co.neweden.LootCrates.listeners.PlayerListener.temp;
import static co.neweden.LootCrates.listeners.PlayerListener.player;

public class ChestSpawner {

    //Spawn a chest with specified items and names
    public void SpawnChest(int luck){
        String ChestName = null;
        Material ItemReceived = null;
        //String ObjectName = null;
        String tier = null;
        int ItemAmount = 0;
        int x = Chances.RandomLocation();
        int y = 100;
        int z = Chances.RandomLocation();

        //One Star loot config Setup
        if (luck == 1){
            ChestName = "Special Loot ☆";
            ItemReceived = ConfigRetriever.common.get(temp);
            //ObjectName = ConfigRetriever.CommonMsg.get(temp);
            tier = "☆";
            ItemAmount = 1;
        }
        //Two Star loot config Setup
        if (luck == 2){
            ChestName = "Special Loot ☆☆";
            ItemReceived = ConfigRetriever.uncommon.get(temp);
            //ObjectName = ConfigRetriever.UncommonMsg.get(temp);
            tier = "☆☆";
            ItemAmount = 1;
        }
        //Three Star loot config Setup
        if (luck == 3){
            ChestName = "Special Loot ☆☆☆";
            ItemReceived = ConfigRetriever.rare.get(temp);
            //ObjectName = ConfigRetriever.RareMsg.get(temp);
            tier = "☆☆☆";
            ItemAmount = 1;
        }

        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        Location chestLoc = new Location(w, x, y, z);
        chestLoc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) chestLoc.getBlock().getState();
        chest.setCustomName(ChestName);
        Inventory ChestInv = chest.getInventory();
        ItemStack ds = null;
        if (ItemReceived != null) {
            ds = new ItemStack(ItemReceived, ItemAmount);
        }
        ItemMeta dm = ds.getItemMeta();
        //dm.setDisplayName(ObjectName);
        ds.setItemMeta(dm);
        ChestInv.setItem(5, ds);

        player.sendMessage("A Special Chest " + tier + " has spawned");

    }
}
