package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static co.neweden.LootCrates.listeners.PlayerListener.Crates;
import static co.neweden.LootCrates.listeners.PlayerListener.temp;
import static co.neweden.LootCrates.listeners.PlayerListener.player;

public class ChestSpawner {
    public static int[ ][ ] ChestLocArray = new int[5][4];
    public static int ArrayIndex = 0;

    //Spawn a chest with specified items and names
    public void SpawnChest(int luck){
        String ChestName = "Chest";
        Material ItemReceived = Material.AIR;
        String tier = "☆";
        int ItemAmount = 0;
        int x = Chances.RandomLocation();
        int y = 100;
        int z = Chances.RandomLocation();

        //One Star loot config Setup
        if (luck == 1){
            ChestName = "Special Loot ☆";
            ItemReceived = ConfigRetriever.OneStar.get(temp);
            tier = "☆";
            ItemAmount = 1;
        }
        //Two Star loot config Setup
        if (luck == 2){
            ChestName = "Special Loot ☆☆";
            ItemReceived = ConfigRetriever.TwoStar.get(temp);
            tier = "☆☆";
            ItemAmount = 1;
        }
        //Three Star loot config Setup
        if (luck == 3){
            ChestName = "Special Rare Loot ☆☆☆";
            ItemReceived = ConfigRetriever.ThreeStar.get(temp);
            tier = "☆☆☆";
            ItemAmount = 1;
        }
        //Four Star loot config Setup
        if (luck == 4){
            ChestName = "Special Rare Loot ☆☆☆☆";
            ItemReceived = ConfigRetriever.FourStar.get(temp);
            tier = "☆☆☆☆";
            ItemAmount = 2;
        }
        //Five Star loot config Setup
        if (luck == 5){
            ChestName = "Special Super Rare Loot ☆☆☆☆☆";
            ItemReceived = ConfigRetriever.FiveStar.get(temp);
            tier = "☆☆☆☆☆";
            ItemAmount = 3;
        }

        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        Location chestLoc = new Location(w, x, y, z);
        chestLoc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) chestLoc.getBlock().getState();
        chest.setCustomName(ChatColor.GREEN + ChestName);
        Inventory ChestInv = chest.getInventory();
        ItemStack ds = new ItemStack(ItemReceived, ItemAmount);
        ItemMeta dm = ds.getItemMeta();
        ds.setItemMeta(dm);
        ChestInv.setItem(Chances.ChestInvSlotRm(), ds);

        //Using for now until i get a db
        ChestLocArray[ArrayIndex][0] = Crates;
        ChestLocArray[ArrayIndex][1] = x;
        ChestLocArray[ArrayIndex][2] = y;
        ChestLocArray[ArrayIndex][3] = z;

        player.sendMessage(ChatColor.BLUE + "DEBUG | Index value: " + ArrayIndex + " | Array Values;Crates: " + ChestLocArray[ArrayIndex][0] + " X: " + ChestLocArray[ArrayIndex][1] + " Y: " + ChestLocArray[ArrayIndex][2] + " Z: " + ChestLocArray[ArrayIndex][3]);
        player.sendMessage("A Special Chest " + tier + " has spawned");
        ArrayIndex = ArrayIndex + 1;
        Timer.OnCrateCreated();

    }



}
