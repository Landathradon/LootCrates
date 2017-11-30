package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static co.neweden.LootCrates.Chances.*;
import static co.neweden.LootCrates.ConfigRetriever.*;
import static co.neweden.LootCrates.Database.*;
import static co.neweden.LootCrates.Main.debugActive;


public class ChestSpawner {

    public static int Crates = 1;

    //Spawn a chest with specified items and names
    private static void SpawnChest(int luck, int num, boolean newChest){
        String ChestName = "Chest";
        int ItemAmount = 0;
        //One Star loot config Setup
        if (luck == 1){
            ChestName = "Special Loot ☆";
            ItemAmount = getRandomAmountItems(1);
        }
        //Two Star loot config Setup
        if (luck == 2){
            ChestName = "Special Loot ☆☆";
            ItemAmount = getRandomAmountItems(2);
        }
        //Three Star loot config Setup
        if (luck == 3){
            ChestName = "Special Rare Loot ☆☆☆";
            ItemAmount = getRandomAmountItems(3);
        }
        //Four Star loot config Setup
        if (luck == 4){
            ChestName = "Special Rare Loot ☆☆☆☆";
            ItemAmount = getRandomAmountItems(4);
        }
        //Five Star loot config Setup
        if (luck == 5){
            ChestName = "Special Super Rare Loot ☆☆☆☆☆";
            ItemAmount = getRandomAmountItems(5);
        }
        checkBelowChestAndPlace(ChestName,ItemAmount, luck, num, newChest);
    }

    private static void checkBelowChestAndPlace(String ChestName, int ItemAmount, int tier, int num, boolean newChest){

        //Checks if there is no water or lava below chest
        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        int retryLimit = 50;
        int chestItemInt = 0;
        for (int retry = 0; retry < retryLimit; retry++) { // We don't want this running indefinitely

            int x = Chances.RandomLocationX();
            int z = Chances.RandomLocationZ();
            int y = getHighestBlockYAt(x,z); // MUST BE ON TOP OF GROUND
            if(getRealCoords(x,z) != null) continue;{

                Location chestLoc = new Location(w, x, y, z);
                Material belowBlock = new Location(chestLoc.getWorld(), chestLoc.getX(), chestLoc.getY() - 1, chestLoc.getZ()).getBlock().getType();
                //Making sure there is not another chest next to it
                Material side1 = new Location(chestLoc.getWorld(), chestLoc.getX() + 1, chestLoc.getY(), chestLoc.getZ()).getBlock().getType();
                Material side2 = new Location(chestLoc.getWorld(), chestLoc.getX() - 1, chestLoc.getY(), chestLoc.getZ()).getBlock().getType();
                Material side3 = new Location(chestLoc.getWorld(), chestLoc.getX(), chestLoc.getY(), chestLoc.getZ() + 1).getBlock().getType();
                Material side4 = new Location(chestLoc.getWorld(), chestLoc.getX(), chestLoc.getY(), chestLoc.getZ() - 1).getBlock().getType();
                if (//Items you don't want the chest to spawn on OR near
                        !belowBlock.equals(Material.WATER) &&
                        !belowBlock.equals(Material.STATIONARY_WATER) &&
                        !belowBlock.equals(Material.LAVA) &&
                        !belowBlock.equals(Material.STATIONARY_LAVA) &&
                        !belowBlock.equals(Material.LEAVES) &&
                        !belowBlock.equals(Material.LEAVES_2) &&
                        !side1.equals(Material.CHEST) &&
                        !side2.equals(Material.CHEST) &&
                        !side3.equals(Material.CHEST) &&
                        !side4.equals(Material.CHEST)) {

                    int chNum = isChestPresent(num);
                    if (chNum < 1 && chNum > MaxCrates) continue;
                    {
                        chestLoc.getBlock().setType(Material.CHEST);
                        Chest chest = (Chest) chestLoc.getBlock().getState();
                        chest.setCustomName(ChatColor.GREEN + ChestName);
                        Inventory ChestInv = chest.getInventory();

                        while (chestItemInt < ItemAmount) {
                            Material ItemReceived = randomItems(tier);
                            ItemStack ds = new ItemStack(ItemReceived, 1);
                            ItemMeta dm = ds.getItemMeta();
                            ds.setItemMeta(dm);
                            ChestInv.setItem(ChestInvSlotRm(ChestInv), ds);
                            chestItemInt++;
                        }

                        ChestClass chClass = new ChestClass();
                        chClass.world = chestLoc.getWorld().getName();
                        chClass.num = num;
                        chClass.x = chestLoc.getBlockX();
                        chClass.y = chestLoc.getBlockY();
                        chClass.z = chestLoc.getBlockZ();
                        chClass.tier = tier;
                        chClass.found = 0;

                        addChestToDatabase(chClass.world, chClass.num, chClass.x, chClass.y, chClass.z, chClass.tier, chClass.found, newChest);
                        cratesMap.put(chestLoc.getBlock(), chClass);
                        debugActive(false, "A Special Chest #" + num + ", Tier " + tier + " has spawned | Try: " + retry, null);
                        Timer.OnCrateCreated(chestLoc.getBlock());
                    }
                    return;
                }
            }
        }
    }

    //Make sure the target is loaded, if not, load it
    static void ensureChunkLoaded(int x, int z, World world) {
        Location rand_location = new Location(world, Double.parseDouble(Integer.toString(x)), 0.0, Double.parseDouble(Integer.toString(z)));
        Chunk chunk = world.getChunkAt(rand_location);
        //To check if loaded and if not load
        if(!world.isChunkLoaded(chunk)) world.loadChunk(chunk);
    }

    public static void CreateChestOnStartup() {

        // Checks if we haven't spawned too many Crates
        while (Crates <= MaxCrates) {

            newChest(Crates,false, true);

        }
        debugActive(true,(Crates-1) + " Crates have been spawned", null);
    }

    public static void newChest(int num, boolean respawn, boolean newChest){

        double chance = ChanceCalc();
        if(!respawn) {num = Crates;}

        // 5% chance Five Star
        if (chance <= 5) {
            debugActive(false,"Chance: " + chance + " %", null);

            SpawnChest(5, num, newChest);
            Crates++;
        }
        // 15% chance Four Star
        else if (chance > 5 && chance <= 20) {
            debugActive(false,"Chance: " + chance + " %", null);

            SpawnChest(4, num, newChest);
            Crates++;
        }
        // 20% chance Three Star
        else if (chance > 20 && chance <= 40){
            debugActive(false,"Chance: " + chance + " %", null);

            SpawnChest(3, num, newChest);
            Crates++;
        }
        // 25% chance Two Star
        else if (chance > 40 && chance <= 65){
            debugActive(false,"Chance: " + chance + " %", null);

            SpawnChest(2, num, newChest);
            Crates++;
        }
        // 35% chance One Star
        else {
            debugActive(false,"Chance: " + chance + " %", null);

            SpawnChest(1, num, newChest);
            Crates++;
        }
    }

    public static String tierCalc(int tier){
        String value = "Null";
        if (tier == 1){
            value = "☆";
        }
        else if (tier == 2){
            value = "☆☆";
        }
        else if (tier == 3){
            value = "☆☆☆";
        }
        else if (tier == 4){
            value = "☆☆☆☆";
        }
        else if (tier == 5){
            value = "☆☆☆☆☆";
        }

        return value;
    }
}
