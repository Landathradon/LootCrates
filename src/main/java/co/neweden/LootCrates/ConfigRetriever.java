package co.neweden.LootCrates;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

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

    //Retrieving everything from the Config.yml File
    public static void getConfigStuff() {
        Main.getPlugin().saveDefaultConfig();
        Configuration config = Main.getPlugin().getConfig();

        //retrieving items from the config
        List<String> listOneStar = config.getStringList("Items.One-Star.Items");
        List<String> listTwoStar = config.getStringList("Items.Two-Star.Items");
        List<String> listThreeStar = config.getStringList("Items.Three-Star.Items");
        List<String> listFourStar = config.getStringList("Items.Four-Star.Items");
        List<String> listFiveStar = config.getStringList("Items.Five-Star.Items");

        //retrieving Max and Min random items values
        MaxItemOneStar = config.getInt("Items.One-Star.Max-Item");
        MinItemOneStar = config.getInt("Items.One-Star.Min-Item");
        MaxItemTwoStar = config.getInt("Items.Two-Star.Max-Item");
        MinItemTwoStar = config.getInt("Items.Two-Star.Min-Item");
        MaxItemThreeStar = config.getInt("Items.Three-Star.Max-Item");
        MinItemThreeStar = config.getInt("Items.Three-Star.Min-Item");
        MaxItemFourStar = config.getInt("Items.Four-Star.Max-Item");
        MinItemFourStar = config.getInt("Items.Four-Star.Min-Item");
        MaxItemFiveStar = config.getInt("Items.Five-Star.Max-Item");
        MinItemFiveStar = config.getInt("Items.Five-Star.Min-Item");

        //retrieving messages from the config
        FoundChest = config.getString("Messages.Found-Chest");
        FoundChest_NB = config.getString("Messages.Found-Chest-NotBreak");
        BreakChest = config.getString("Messages.Break-Chest");

        //MYSQL config
        username = config.getString("mysql.username", "");
        password = config.getString("mysql.password", "");
        port = config.getString("mysql.port");
        host = config.getString("mysql.host", "");
        database = config.getString("mysql.database", "");

        //adding the items to their rarity list
        //One Star Crate List
        OneStar.clear();
        for (String aListOneStar : listOneStar) {
            OneStar.add(Material.getMaterial(aListOneStar));
        }

        //Two Star Crate List
        TwoStar.clear();
        for (String aListTwoStar : listTwoStar) {
            TwoStar.add(Material.getMaterial(aListTwoStar));
        }

        //Three Star Crate List
        ThreeStar.clear();
        for (String aListThreeStar : listThreeStar) {
            ThreeStar.add(Material.getMaterial(aListThreeStar));
        }

        //Four Star Crate List
        FourStar.clear();
        for (String aListFourStar : listFourStar) {
            FourStar.add(Material.getMaterial(aListFourStar));
        }

        //Five Star Crate List
        FiveStar.clear();
        for (String aListFiveStar : listFiveStar) {
            FiveStar.add(Material.getMaterial(aListFiveStar));
        }

        //Other Stuff
        WorldConfig = config.getString("Worlds");
        Debug = config.getBoolean("Debug");
        Enable_Plugin = config.getBoolean("Enable-Plugin");
        MaxCrates = config.getInt("Crates.max-amount");
        MaxSpawnTime = config.getDouble("Crates.max-spawned-time");
        MinSpawnTime = config.getDouble("Crates.min-spawned-time");

        //Max Distance Allowed
        max_x = config.getInt("Crates.max-x-distance");
        min_x = config.getInt("Crates.min-x-distance");
        max_z = config.getInt("Crates.max-z-distance");
        min_z = config.getInt("Crates.min-z-distance");
    }

    public static void checkConfig(boolean activeMessages){

        if(MinSpawnTime > MaxSpawnTime){
            if(activeMessages) {
                Main.debugActive(true, "!!! Make sure the values in max-spawned-time and min-spawned-time are correct in the config !!!", null);
            }
            MinSpawnTime = 5.0;
            MaxSpawnTime = 20.0;
        }
        if(MinItemOneStar > MaxItemOneStar){
            if(activeMessages) {
                Main.debugActive(true, "!!! Make sure the values in One-Star Max-Items and Min-Items are correct in the config !!!", null);
            }
            MinItemOneStar = 1;
            MaxItemOneStar = 4;
        }
        if(MinItemTwoStar > MaxItemTwoStar){
            if(activeMessages) {
                Main.debugActive(true, "!!! Make sure the values in Two-Star Max-Items and Min-Items are correct in the config !!!", null);
            }
            MinItemTwoStar = 1;
            MaxItemTwoStar = 4;
        }
        if(MinItemThreeStar > MaxItemThreeStar){
            if(activeMessages) {
                Main.debugActive(true, "!!! Make sure the values in Three-Star Max-Items and Min-Items are correct in the config !!!", null);
            }
            MinItemThreeStar = 1;
            MaxItemThreeStar = 3;
        }
        if(MinItemFourStar > MaxItemFourStar){
            if(activeMessages) {
                Main.debugActive(true, "!!! Make sure the values in Four-Star Max-Items and Min-Items are correct in the config !!!", null);
            }
            MinItemFourStar = 1;
            MaxItemFourStar = 3;
        }
        if(MinItemFiveStar > MaxItemFiveStar){
            if(activeMessages) {
                Main.debugActive(true, "!!! Make sure the values in Five-Star Max-Items and Min-Items are correct in the config !!!", null);
            }
            MinItemFiveStar = 1;
            MaxItemFiveStar = 2;
        }
        if(!Enable_Plugin){
            if(activeMessages) {
                Main.debugActive(true, "!!! You have set the plugin as disabled in the config !!!", null);
            }
            Main.disablePlugin();

        }
        if(username.equalsIgnoreCase("user") && password.equalsIgnoreCase("pass")){
            if(activeMessages) {
                Main.debugActive(true, "!!! You need to change your username and password in the config !!!", null);
                Main.debugActive(true, "!!! Username: user and Password: pass are not secure enough !!!", null);
            }
            Main.disablePlugin();
        }
    }
}
