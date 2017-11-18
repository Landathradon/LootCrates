package co.neweden.LootCrates;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

import static co.neweden.LootCrates.main.debugActive;
import static co.neweden.LootCrates.main.disablePlugin;

public class ConfigRetriever {

    //Max & Min amount of items
    static int MaxItemOneStar;
    static int MinItemOneStar;
    static int MaxItemTwoStar;
    static int MinItemTwoStar;
    static int MaxItemThreeStar;
    static int MinItemThreeStar;
    static int MaxItemFourStar;
    static int MinItemFourStar;
    static int MaxItemFiveStar;
    static int MinItemFiveStar;

    //MYSQL
    static String username;
    static String password;
    static String port;
    static String host;
    static String database;

    //Other Stuff
    static String WorldConfig;
    static boolean Debug;
    private static boolean Enable_Plugin;
    static int MaxCrates;
    static double MaxSpawnTime;
    static double MinSpawnTime;
    public static String lootcratesPrefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "LootCrates" + ChatColor.GRAY + "] ";

    //Messages
    public static String FoundChest;
    public static String FoundChest_NB;
    public static String BreakChest;

    //Max Distance Allowed
    static int max_x;
    static int min_x;
    static int max_z;
    static int min_z;

    //Item list
    static ArrayList<Material> OneStar = new ArrayList<>();
    static ArrayList<Material> TwoStar = new ArrayList<>();
    static ArrayList<Material> ThreeStar = new ArrayList<>();
    static ArrayList<Material> FourStar = new ArrayList<>();
    static ArrayList<Material> FiveStar = new ArrayList<>();

    //Constructor | Retrieving everything from the Config.yml File
    ConfigRetriever(main pl) {

        //retrieving items from the config
        List<String> listOneStar = pl.getConfig().getStringList("Items.One-Star.Items");
        List<String> listTwoStar = pl.getConfig().getStringList("Items.Two-Star.Items");
        List<String> listThreeStar = pl.getConfig().getStringList("Items.Three-Star.Items");
        List<String> listFourStar = pl.getConfig().getStringList("Items.Four-Star.Items");
        List<String> listFiveStar = pl.getConfig().getStringList("Items.Five-Star.Items");

        //retrieving Max and Min random items values
        MaxItemOneStar = pl.getConfig().getInt("Items.One-Star.Max-Item");
        MinItemOneStar = pl.getConfig().getInt("Items.One-Star.Min-Item");
        MaxItemTwoStar = pl.getConfig().getInt("Items.Two-Star.Max-Item");
        MinItemTwoStar = pl.getConfig().getInt("Items.Two-Star.Min-Item");
        MaxItemThreeStar = pl.getConfig().getInt("Items.Three-Star.Max-Item");
        MinItemThreeStar = pl.getConfig().getInt("Items.Three-Star.Min-Item");
        MaxItemFourStar = pl.getConfig().getInt("Items.Four-Star.Max-Item");
        MinItemFourStar = pl.getConfig().getInt("Items.Four-Star.Min-Item");
        MaxItemFiveStar = pl.getConfig().getInt("Items.Five-Star.Max-Item");
        MinItemFiveStar = pl.getConfig().getInt("Items.Five-Star.Min-Item");

        //retrieving messages from the config
        FoundChest = pl.getConfig().getString("Messages.Found-Chest");
        FoundChest_NB = pl.getConfig().getString("Messages.Found-Chest-NotBreak");
        BreakChest = pl.getConfig().getString("Messages.Break-Chest");

        //MYSQL config
        username = pl.getConfig().getString("mysql.username");
        password = pl.getConfig().getString("mysql.password");
        port = pl.getConfig().getString("mysql.port");
        host = pl.getConfig().getString("mysql.host");
        database = pl.getConfig().getString("mysql.database");

        //adding the items to their rarity list
        //One Star Crate List
        OneStar.add(Material.getMaterial(listOneStar.get(0)));
        OneStar.add(Material.getMaterial(listOneStar.get(1)));

        //Two Star Crate List
        TwoStar.add(Material.getMaterial(listTwoStar.get(0)));
        TwoStar.add(Material.getMaterial(listTwoStar.get(1)));

        //Three Star Crate List
        ThreeStar.add(Material.getMaterial(listThreeStar.get(0)));
        ThreeStar.add(Material.getMaterial(listThreeStar.get(1)));

        //Four Star Crate List
        FourStar.add(Material.getMaterial(listFourStar.get(0)));
        FourStar.add(Material.getMaterial(listFourStar.get(1)));

        //Five Star Crate List
        FiveStar.add(Material.getMaterial(listFiveStar.get(0)));
        FiveStar.add(Material.getMaterial(listFiveStar.get(1)));

        //Other Stuff
        WorldConfig = pl.getConfig().getString("Worlds");
        Debug = pl.getConfig().getBoolean("Debug");
        Enable_Plugin = pl.getConfig().getBoolean("Enable-Plugin");
        MaxCrates = pl.getConfig().getInt("Crates.max-amount");
        MaxSpawnTime = pl.getConfig().getDouble("Crates.max-spawned-time");
        MinSpawnTime = pl.getConfig().getDouble("Crates.min-spawned-time");

        //Max Distance Allowed
        max_x = pl.getConfig().getInt("Crates.max-x-distance");
        min_x = pl.getConfig().getInt("Crates.min-x-distance");
        max_z = pl.getConfig().getInt("Crates.max-z-distance");
        min_z = pl.getConfig().getInt("Crates.min-z-distance");
    }

    static void checkConfig(int val){

        if(MinSpawnTime > MaxSpawnTime){
            if(val == 1) {
                debugActive(true, "!!! Make sure the values in max-spawned-time and min-spawned-time are correct in the config !!!");
            }
            disablePlugin();
        }
        if(MinItemOneStar > MaxItemOneStar){
            if(val == 1) {
                debugActive(true, "!!! Make sure the values in One-Star Max-Items and Min-Items are correct in the config !!!");
            }
            disablePlugin();

        }
        if(MinItemTwoStar > MaxItemTwoStar){
            if(val == 1) {
                debugActive(true, "!!! Make sure the values in Two-Star Max-Items and Min-Items are correct in the config !!!");
            }
            disablePlugin();

        }
        if(MinItemThreeStar > MaxItemThreeStar){
            if(val == 1) {
                debugActive(true, "!!! Make sure the values in Three-Star Max-Items and Min-Items are correct in the config !!!");
            }
            disablePlugin();

        }
        if(MinItemFourStar > MaxItemFourStar){
            if(val == 1) {
                debugActive(true, "!!! Make sure the values in Four-Star Max-Items and Min-Items are correct in the config !!!");
            }
            disablePlugin();

        }
        if(MinItemFiveStar > MaxItemFiveStar){
            if(val == 1) {
                debugActive(true, "!!! Make sure the values in Five-Star Max-Items and Min-Items are correct in the config !!!");
            }
            disablePlugin();

        }
        if(!Enable_Plugin){
            if(val == 1) {
                debugActive(true, "!!! You have set the plugin as disabled in the config !!!");
            }
            disablePlugin();

        }
        if(username.equalsIgnoreCase("user") && password.equalsIgnoreCase("pass")){
            if(val == 1) {
                debugActive(true, "!!! You need to change your username and password in the config !!!");
                debugActive(true, "!!! Username: user and Password: pass are not secure enough !!!");
            }
            disablePlugin();
        }
    }
}
