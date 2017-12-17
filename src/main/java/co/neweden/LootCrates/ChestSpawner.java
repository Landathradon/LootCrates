package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChestSpawner {

    //Spawn a chest with specified items and names
    private static void SpawnChest(int luck){
        String ChestName = "Chest";
        int ItemAmount = 0;
        //One Star loot config Setup
        if (luck == 1){
            ChestName = "Special Loot ☆";
            ItemAmount = Chances.getRandomAmountItems(1);
        }
        //Two Star loot config Setup
        if (luck == 2){
            ChestName = "Special Loot ☆☆";
            ItemAmount = Chances.getRandomAmountItems(2);
        }
        //Three Star loot config Setup
        if (luck == 3){
            ChestName = "Special Rare Loot ☆☆☆";
            ItemAmount = Chances.getRandomAmountItems(3);
        }
        //Four Star loot config Setup
        if (luck == 4){
            ChestName = "Special Rare Loot ☆☆☆☆";
            ItemAmount = Chances.getRandomAmountItems(4);
        }
        //Five Star loot config Setup
        if (luck == 5){
            ChestName = "Special Super Rare Loot ☆☆☆☆☆";
            ItemAmount = Chances.getRandomAmountItems(5);
        }
        checkBelowChestAndPlace(ChestName,ItemAmount, luck);
    }

    private static void checkBelowChestAndPlace(String ChestName, int ItemAmount, int tier) {

        //Checks if there is no water or lava below chest
        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        int retryLimit = 50;
        int chestItemInt = 0;
        for (int retry = 0; retry < retryLimit; retry++) { // We don't want this running indefinitely

            int x = Chances.RandomLocationX();
            int z = Chances.RandomLocationZ();
            int y = Chances.getHighestBlockYAt(x, z); // MUST BE ON TOP OF GROUND
            if (Chances.getRealCoords(x, z) != null) continue;

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

                    chestLoc.getBlock().setType(Material.CHEST);
                    Chest chest = (Chest) chestLoc.getBlock().getState();
                    chest.setCustomName(ChatColor.GREEN + ChestName);
                    Inventory ChestInv = chest.getInventory();

                    while (chestItemInt < ItemAmount) {
                        Material ItemReceived = Chances.randomItems(tier);
                        ItemStack ds = new ItemStack(ItemReceived, 1);
                        ItemMeta dm = ds.getItemMeta();
                        ds.setItemMeta(dm);
                        ChestInv.setItem(Chances.ChestInvSlotRm(ChestInv), ds);
                        chestItemInt++;
                    }

                    Database.ChestClass chClass = new Database.ChestClass();
                    chClass.world = chestLoc.getWorld().getName();
                    chClass.x = chestLoc.getBlockX();
                    chClass.y = chestLoc.getBlockY();
                    chClass.z = chestLoc.getBlockZ();
                    chClass.tier = tier;
                    chClass.found = false;

                    Database.addChestToDatabase(chClass.world, chestLoc.getBlock(), chClass.tier);
                    Database.cratesMap.put(chestLoc.getBlock(), chClass);
                    Main.debugActive(false, "A Special Chest Tier " + tier + " has spawned | Try: " + retry, null);
                    Timer.OnCrateCreated(chestLoc.getBlock());

                return;
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
        while (Database.getCurrentChestsCount() <= ConfigRetriever.MaxCrates) {
            newChest();
        }
        Main.debugActive(true,(Database.getCurrentChestsCount()) + " Crates have been spawned", null);
    }

    public static void newChest(){

        double chance = Chances.ChanceCalc();

        // 5% chance Five Star
        if (chance <= 5) {
            Main.debugActive(false,"Chance: " + chance + " %", null);
            SpawnChest(5);
        }
        // 15% chance Four Star
        else if (chance > 5 && chance <= 20) {
            Main.debugActive(false,"Chance: " + chance + " %", null);
            SpawnChest(4);
        }
        // 20% chance Three Star
        else if (chance > 20 && chance <= 40){
            Main.debugActive(false,"Chance: " + chance + " %", null);
            SpawnChest(3);
        }
        // 25% chance Two Star
        else if (chance > 40 && chance <= 65){
            Main.debugActive(false,"Chance: " + chance + " %", null);
            SpawnChest(2);
        }
        // 35% chance One Star
        else {
            Main.debugActive(false,"Chance: " + chance + " %", null);
            SpawnChest(1);
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
