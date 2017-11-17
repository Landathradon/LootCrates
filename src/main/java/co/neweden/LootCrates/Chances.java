package co.neweden.LootCrates;

import co.neweden.LandManager.LandClaim;
import co.neweden.LandManager.LandManager;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static co.neweden.LootCrates.ChestSpawner.ensureChunkLoaded;
import static co.neweden.LootCrates.ConfigRetriever.*;

class Chances {


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

    static int ChestInvSlotRm(){
        return ThreadLocalRandom.current().nextInt(0,26  + 1);
    }

    //Will calculate if the coords are within any protected areas
    static LandClaim getRealCoords(int x, int z){

        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        Location loc = new Location(w, x,0, z);
        Chunk chunk = loc.getChunk();
        LandClaim val = LandManager.getLandClaim(chunk);

        return val;
    }

    static int getHighestBlockYAt(int x, int z) {
        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        ensureChunkLoaded(x,z,w);
        return w.getHighestBlockYAt(x, z);
    }

    static int getSizeArrayList(ArrayList array){
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

}
