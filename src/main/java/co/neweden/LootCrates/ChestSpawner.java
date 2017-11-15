package co.neweden.LootCrates;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static co.neweden.LootCrates.Chances.getHighestBlockYAt;
import static co.neweden.LootCrates.ConfigRetriever.MaxCrates;
import static co.neweden.LootCrates.main.debugActive;


public class ChestSpawner {
    private static main plugin;
    ChestSpawner(main pl) {
        plugin = pl;
    }

    public static int temp = Math.random() < 0.5 ? 0 : 1; //use the length of the array instead
    public static int Crates = 1;

    //Spawn a chest with specified items and names
    public static void SpawnChest(int luck){
        String ChestName = "Chest";
        Material ItemReceived = Material.AIR;
        String tier = "</1/>";
        int ItemAmount = 0;

        //One Star loot config Setup
        if (luck == 1){
            ChestName = "Special Loot ☆";
            ItemReceived = ConfigRetriever.OneStar.get(temp);
            tier = "</1/>";
            ItemAmount = 1;
        }
        //Two Star loot config Setup
        if (luck == 2){
            ChestName = "Special Loot ☆☆";
            ItemReceived = ConfigRetriever.TwoStar.get(temp);
            tier = "</2/>";
            ItemAmount = 1;
        }
        //Three Star loot config Setup
        if (luck == 3){
            ChestName = "Special Rare Loot ☆☆☆";
            ItemReceived = ConfigRetriever.ThreeStar.get(temp);
            tier = "</3/>";
            ItemAmount = 1;
        }
        //Four Star loot config Setup
        if (luck == 4){
            ChestName = "Special Rare Loot ☆☆☆☆";
            ItemReceived = ConfigRetriever.FourStar.get(temp);
            tier = "</4/>";
            ItemAmount = 2;
        }
        //Five Star loot config Setup
        if (luck == 5){
            ChestName = "Special Super Rare Loot ☆☆☆☆☆";
            ItemReceived = ConfigRetriever.FiveStar.get(temp);
            tier = "</5/>";
            ItemAmount = 3;
        }

        checkBelowChestAndPlace(ChestName,ItemReceived,tier,ItemAmount, luck);


    }

    public static void checkBelowChestAndPlace(String ChestName, Material ItemReceived, String tier, int ItemAmount, int luck){

        //Checks if there is no water or lava below chest
        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        int retry = 0;
        int retryLimit = 50;
        while (retry < retryLimit) { // We don't want this running indefinitely

            int x = Chances.RandomLocationX();
            int z = Chances.RandomLocationZ();
            int y = getHighestBlockYAt(x,z); // MUST BE ON TOP OF GROUND
            Location chestLoc = new Location(w, x, y, z);
            Material belowBlock = new Location(chestLoc.getWorld(), chestLoc.getX(), chestLoc.getY() - 1, chestLoc.getZ()).getBlock().getType();
            if (
                                //Items you dont want the chest to spawn on
                                !belowBlock.equals(Material.WATER) &&
                                !belowBlock.equals(Material.STATIONARY_WATER) &&
                                !belowBlock.equals(Material.LAVA) &&
                                !belowBlock.equals(Material.STATIONARY_LAVA) &&
                                !belowBlock.equals(Material.LEAVES) //Need to check for all leaves and this doesn't work
                    )
            {
                chestLoc.getBlock().setType(Material.CHEST);
                Chest chest = (Chest) chestLoc.getBlock().getState();
                chest.setCustomName(ChatColor.GREEN + ChestName);
                Inventory ChestInv = chest.getInventory();
                ItemStack ds = new ItemStack(ItemReceived, ItemAmount);
                ItemMeta dm = ds.getItemMeta();
                ds.setItemMeta(dm);
                ChestInv.setItem(Chances.ChestInvSlotRm(), ds);

                Database.addChestToDatabase(chestLoc.getWorld().getName(), Crates, x, y, z, luck);

                debugActive(false,"A Special Chest " + tier + " has spawned | Try: " + retry);
                Timer.OnCrateCreated(Crates, false);
                return;
            } else {
                retry++;
            }
        }
    }

    //Make sure the target is loaded, if not, load it
    public static void ensureChunkLoaded(int x, int z, World world) {
        Location randlocation = new Location(world, Double.parseDouble(Integer.toString(x)), 0.0, Double.parseDouble(Integer.toString(z)));
        Chunk chunk = world.getChunkAt(randlocation);
        //To check if loaded and if not load
        if(!world.isChunkLoaded(chunk)) world.loadChunk(chunk);
    }

    public static void CreateChestOnStartup() {

            // Checks if we haven't spawned too many Crates
            while (Crates <= MaxCrates) {

                double d = Chances.ChanceCalc();

                // 20% chance Five Star
                if (d <= 0.20) {
                    debugActive(false,"You are so lucky !! : " + (d * 100) + " %");

                    SpawnChest(5);
                    Crates = Crates + 1;
                }

                // 20% chance Four Star
                else if (d > 0.20 && d <= 0.40) {
                    debugActive(false,"Wow ! : " + (d * 100) + " %");

                    SpawnChest(4);
                    Crates = Crates + 1;
                }

                // 20% chance Three Star
                else if (d > 0.40 && d <= 0.60){
                    debugActive(false,"Not so bad : " + (d * 100) + " %");

                    SpawnChest(3);
                    Crates = Crates + 1;
                }
                // 20% chance Two Star
                else if (d > 0.60 && d <= 0.80){
                    debugActive(false,"Better next time : " + (d * 100) + " %");

                    SpawnChest(2);
                    Crates = Crates + 1;
                }
                // 20% chance One Star
                else {
                    debugActive(false,"Meh (╯°□°）╯︵ ┻━┻ : " + (d * 100) + " %");

                    SpawnChest(1);
                    Crates = Crates + 1;
                }

            }
            // if we have reached MaxCrates set in config it will throw this error
//            else{
//                plugin.getLogger().info("You have reached the max amount of crates that can be spawned at once !");
//            }


    }

}
