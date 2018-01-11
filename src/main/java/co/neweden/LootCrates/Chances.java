package co.neweden.LootCrates;

import co.neweden.LandManager.LandClaim;
import co.neweden.LandManager.LandManager;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Chances {

    //calculate the chance to receive a good drop
    static double ChanceCalc() {

        double val = Math.random();
        val = Math.round(val * 100.0);

        return val;
    }

    //calculate random spawn points for the chest
    static int RandomLocationX() {
        return ThreadLocalRandom.current().nextInt(ConfigRetriever.min_x, ConfigRetriever.max_x);
    }

    static int RandomLocationZ() {
        return ThreadLocalRandom.current().nextInt(ConfigRetriever.min_z, ConfigRetriever.max_z);
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
    static LandClaim getRealCoords(int x, int z, World w) {
        Location loc = new Location(w, x, 0, z);
        Chunk chunk = loc.getChunk();

        return LandManager.getLandClaim(chunk);
    }

    static int getHighestBlockYAt(int x, int z, World w) {
        ChestSpawner.ensureChunkLoaded(x,z,w);
        return w.getHighestBlockYAt(x,z);
    }

    private static int getSizeArrayList(ArrayList array){
        Random r = new Random();
        int temp;
        temp = r.nextInt(array.size());
        return temp;
    }

    static long randomDespawnTime(){
        long min_time = (long) ((ConfigRetriever.MinSpawnTime*60)*20);
        long max_time = (long) ((ConfigRetriever.MaxSpawnTime*60)*20);

        return ThreadLocalRandom.current().nextLong(min_time, max_time);
    }

    static int getRandomAmountItems(int tier){
        switch (tier){
            case 1:
                return ThreadLocalRandom.current().nextInt(ConfigRetriever.MinItemOneStar, ConfigRetriever.MaxItemOneStar);
            case 2:
                return ThreadLocalRandom.current().nextInt(ConfigRetriever.MinItemTwoStar, ConfigRetriever.MaxItemTwoStar);
            case 3:
                return ThreadLocalRandom.current().nextInt(ConfigRetriever.MinItemThreeStar, ConfigRetriever.MaxItemThreeStar);
            case 4:
                return ThreadLocalRandom.current().nextInt(ConfigRetriever.MinItemFourStar, ConfigRetriever.MaxItemFourStar);
            case 5:
                return ThreadLocalRandom.current().nextInt(ConfigRetriever.MinItemFiveStar, ConfigRetriever.MaxItemFiveStar);
            default:
                break;
        }
        return 1;
    }

    static Material randomItems(int tier){

        int temp;
        switch (tier){
            case 1:
                temp = getSizeArrayList(ConfigRetriever.OneStar);
                return ConfigRetriever.OneStar.get(temp);
            case 2:
                temp = getSizeArrayList(ConfigRetriever.TwoStar);
                return ConfigRetriever.TwoStar.get(temp);
            case 3:
                temp = getSizeArrayList(ConfigRetriever.ThreeStar);
                return ConfigRetriever.ThreeStar.get(temp);
            case 4:
                temp = getSizeArrayList(ConfigRetriever.FourStar);
                return ConfigRetriever.FourStar.get(temp);
            case 5:
                temp = getSizeArrayList(ConfigRetriever.FiveStar);
                return ConfigRetriever.FiveStar.get(temp);
            default:
                return Material.AIR;
        }
    }
}
