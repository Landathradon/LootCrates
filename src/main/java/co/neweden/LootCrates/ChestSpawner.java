package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static co.neweden.LootCrates.Chances.getHighestBlockYAt;
import static co.neweden.LootCrates.Chances.getRealCoords;
import static co.neweden.LootCrates.Chances.getSizeArrayList;
import static co.neweden.LootCrates.ConfigRetriever.MaxCrates;
import static co.neweden.LootCrates.ConfigRetriever.OneStar;
import static co.neweden.LootCrates.main.debugActive;


public class ChestSpawner {

    private static int temp;
    public static int Crates = 1;

    //Spawn a chest with specified items and names
    private static void SpawnChest(int luck, int chNum){
        String ChestName = "Chest";
        Material ItemReceived = Material.AIR;
        int tier = 1;
        int ItemAmount = 0;
        //One Star loot config Setup
        if (luck == 1){
            ChestName = "Special Loot ☆";
            temp = getSizeArrayList(OneStar);
            ItemReceived = ConfigRetriever.OneStar.get(temp);
            tier = 1;
            ItemAmount = 1;
        }
        //Two Star loot config Setup
        if (luck == 2){
            ChestName = "Special Loot ☆☆";
            ItemReceived = ConfigRetriever.TwoStar.get(temp);
            tier = 2;
            ItemAmount = 1;
        }
        //Three Star loot config Setup
        if (luck == 3){
            ChestName = "Special Rare Loot ☆☆☆";
            ItemReceived = ConfigRetriever.ThreeStar.get(temp);
            tier = 3;
            ItemAmount = 1;
        }
        //Four Star loot config Setup
        if (luck == 4){
            ChestName = "Special Rare Loot ☆☆☆☆";
            ItemReceived = ConfigRetriever.FourStar.get(temp);
            tier = 4;
            ItemAmount = 2;
        }
        //Five Star loot config Setup
        if (luck == 5){
            ChestName = "Special Super Rare Loot ☆☆☆☆☆";
            ItemReceived = ConfigRetriever.FiveStar.get(temp);
            tier = 5;
            ItemAmount = 3;
        }

        checkBelowChestAndPlace(ChestName,ItemReceived,tier,ItemAmount, luck, chNum);


    }

    private static void checkBelowChestAndPlace(String ChestName, Material ItemReceived, int tier, int ItemAmount, int luck, int chNum){

        //Checks if there is no water or lava below chest
        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        int retry = 0;
        int retryLimit = 50;
        while (retry < retryLimit) { // We don't want this running indefinitely

            int x = Chances.RandomLocationX();
            int z = Chances.RandomLocationZ();
            int y = getHighestBlockYAt(x,z); // MUST BE ON TOP OF GROUND
            if(getRealCoords(x,z) == null) {

                Location chestLoc = new Location(w, x, y, z);
                Material belowBlock = new Location(chestLoc.getWorld(), chestLoc.getX(), chestLoc.getY() - 1, chestLoc.getZ()).getBlock().getType();
                if (
                    //Items you dont want the chest to spawn on
                        !belowBlock.equals(Material.WATER) &&
                                !belowBlock.equals(Material.STATIONARY_WATER) &&
                                !belowBlock.equals(Material.LAVA) &&
                                !belowBlock.equals(Material.STATIONARY_LAVA) &&
                                !belowBlock.equals(Material.LEAVES) &&
                                !belowBlock.equals(Material.LEAVES_2)
                        ) {
                    chestLoc.getBlock().setType(Material.CHEST);
                    Chest chest = (Chest) chestLoc.getBlock().getState();
                    chest.setCustomName(ChatColor.GREEN + ChestName);
                    Inventory ChestInv = chest.getInventory();
                    ItemStack ds = new ItemStack(ItemReceived, ItemAmount);
                    ItemMeta dm = ds.getItemMeta();
                    ds.setItemMeta(dm);
                    ChestInv.setItem(Chances.ChestInvSlotRm(), ds);

                    Database.addChestToDatabase(chestLoc.getWorld().getName(), chNum, x, y, z, luck);

                    debugActive(false, "A Special Chest " + tier + " has spawned | Try: " + retry);
                    Timer.OnCrateCreated(chNum, false);
                    return;
                } else {
                    retry++;
                }
            } else{
                retry++;
            }
        }
    }

    //Make sure the target is loaded, if not, load it
    static void ensureChunkLoaded(int x, int z, World world) {
        Location randlocation = new Location(world, Double.parseDouble(Integer.toString(x)), 0.0, Double.parseDouble(Integer.toString(z)));
        Chunk chunk = world.getChunkAt(randlocation);
        //To check if loaded and if not load
        if(!world.isChunkLoaded(chunk)) world.loadChunk(chunk);
    }

    public static void CreateChestOnStartup() {

        // Checks if we haven't spawned too many Crates
        while (Crates <= MaxCrates) {

            newChest(Crates);

        }
    }

    public static void newChest(int chNum){

        double chance = Chances.ChanceCalc();

        // 20% chance Five Star
        if (chance <= 20) {
            debugActive(false,"Chance: " + chance + " %");

            SpawnChest(5, chNum);
            Crates = Crates + 1;
        }

        // 20% chance Four Star
        else if (chance > 20 && chance <= 40) {
            debugActive(false,"Chance: " + chance + " %");

            SpawnChest(4, chNum);
            Crates = Crates + 1;
        }

        // 20% chance Three Star
        else if (chance > 40 && chance <= 60){
            debugActive(false,"Chance: " + chance + " %");

            SpawnChest(3, chNum);
            Crates = Crates + 1;
        }
        // 20% chance Two Star
        else if (chance > 60 && chance <= 80){
            debugActive(false,"Chance: " + chance + " %");

            SpawnChest(2, chNum);
            Crates = Crates + 1;
        }
        // 20% chance One Star
        else {
            debugActive(false,"Chance: " + chance + " %");

            SpawnChest(1, chNum);
            Crates = Crates + 1;
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
