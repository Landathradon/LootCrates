package co.neweden.LootCrates;

import org.bukkit.*;

import java.util.concurrent.ThreadLocalRandom;

import static co.neweden.LootCrates.ChestSpawner.ensureChunkLoaded;
import static co.neweden.LootCrates.ConfigRetriever.*;

public class Chances {


    //calculate the chance to receive a good drop
    public static double ChanceCalc() {

        double d = Math.random();
        d = Math.round(d * 100.0) / 100.0;

        return d;
    }

    //calculate random spawn points for the chest | MUST IMPLEMENT LANDMANAGER FOR GOOD COORDS
    public static int RandomLocationX() {
        return ThreadLocalRandom.current().nextInt(min_x, max_x);
    }

    public static int RandomLocationZ() {
        return ThreadLocalRandom.current().nextInt(min_z, max_z);
    }

    public static int ChestInvSlotRm(){
        return ThreadLocalRandom.current().nextInt(0,26  + 1);
    }

    //Will calculate if the coords are within any protected areas
    public static int[] getRealCoords(){
        int[] value = new int[2];
        int temp_x = RandomLocationX();
        int temp_z = RandomLocationZ();
        int LM_XMAX = 2;
        int LM_XMIN = 0;
        int LM_ZMAX = 2;
        int LM_ZMIN = 0;

        if(temp_x > LM_XMIN && temp_x < LM_XMAX && temp_z > LM_ZMIN && temp_z < LM_ZMAX){

            value[0] = 1; //This function needs to be completed before use (value[0] = x; value[1] = z;)

        }

        return value;
    }

    public static int getHighestBlockYAt(int x, int z) {
        World w = Bukkit.getWorld(ConfigRetriever.WorldConfig);
        ensureChunkLoaded(x,z,w);
        return w.getHighestBlockYAt(x, z);
    }

}
