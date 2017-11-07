package co.neweden.LootCrates;

import java.util.concurrent.ThreadLocalRandom;

public class Chances {


    //calculate the chance to receive a good drop
    public static double ChanceCalc() {

        double d = Math.random();
        d = Math.round(d * 100.0) / 100.0;

        return d;
    }

    //calculate random spawn points for the chest
    public static int RandomLocation() {

        return ThreadLocalRandom.current().nextInt(0, 100 + 1);
    }

    public static int ChestInvSlotRm(){
        return ThreadLocalRandom.current().nextInt(0,26  + 1);
    }


}
