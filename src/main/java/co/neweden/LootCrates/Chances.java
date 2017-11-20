package co.neweden.LootCrates;

import co.neweden.LandManager.LandClaim;
import co.neweden.LandManager.LandManager;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static co.neweden.LootCrates.ChestSpawner.ensureChunkLoaded;
import static co.neweden.LootCrates.ConfigRetriever.*;

class Chances {
    private static Main plugin;

    Chances(Main pl) {
        plugin = pl;
    }


    //calculate the chance to receive a good drop
    static double ChanceCalc() {

        double val = Math.random();
        val = Math.round(val * 100.0);

        return val;
    }

    //calculate random spawn points for the chest
    static int RandomLocationX() {
        return ThreadLocalRandom.current().nextInt(min_x, max_x);
    }

    static int RandomLocationZ() {
        return ThreadLocalRandom.current().nextInt(min_z, max_z);
    }

    static int ChestInvSlotRm(Inventory inv){
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int rm = random.nextInt(inv.getSize());
            if (inv.getItem(rm) == null) {
                return rm;
            }
        }
        return 1;
    }

    //Will calculate if the coords are within any protected areas
    static LandClaim getRealCoords(int x, int z){

            World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
            Location loc = new Location(w, x,0, z);
            Chunk chunk = loc.getChunk();

            return LandManager.getLandClaim(chunk);
    }

    static int getHighestBlockYAt(int x, int z) {
        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        ensureChunkLoaded(x,z,w);
        return w.getHighestBlockYAt(x, z);
    }

    private static int getSizeArrayList(ArrayList array){
        Random r = new Random();
        int temp;
        temp = r.nextInt(array.size());
        return temp;
    }

    static long randomDespawnTime(){
        long min_time = (long) ((MinSpawnTime*60)*20);
        long max_time = (long) ((MaxSpawnTime*60)*20);

        return ThreadLocalRandom.current().nextLong(min_time, max_time);
    }

    static int getRandomAmountItems(int tier){
        switch (tier){
            case 1:
                return ThreadLocalRandom.current().nextInt(MinItemOneStar,MaxItemOneStar);
            case 2:
                return ThreadLocalRandom.current().nextInt(MinItemTwoStar,MaxItemTwoStar);
            case 3:
                return ThreadLocalRandom.current().nextInt(MinItemThreeStar,MaxItemThreeStar);
            case 4:
                return ThreadLocalRandom.current().nextInt(MinItemFourStar,MaxItemFourStar);
            case 5:
                return ThreadLocalRandom.current().nextInt(MinItemFiveStar,MaxItemFiveStar);
            default:
                break;
        }
        return 1;
    }

    static Material randomItems(int tier){

        int temp;
        switch (tier){
            case 1:
                temp = getSizeArrayList(OneStar);
                return OneStar.get(temp);
            case 2:
                temp = getSizeArrayList(TwoStar);
                return TwoStar.get(temp);
            case 3:
                temp = getSizeArrayList(ThreeStar);
                return ThreeStar.get(temp);
            case 4:
                temp = getSizeArrayList(FourStar);
                return FourStar.get(temp);
            case 5:
                temp = getSizeArrayList(FiveStar);
                return FiveStar.get(temp);
            default:
                break;
        }

        return Material.AIR;
    }
}
