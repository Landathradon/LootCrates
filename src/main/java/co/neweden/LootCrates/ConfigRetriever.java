package co.neweden.LootCrates;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

import static co.neweden.LootCrates.main.debugActive;
import static co.neweden.LootCrates.main.disablePlugin;

public class ConfigRetriever {
    private main plugin;

    //Item list
    public static List<String> listOneStar;
    public static List<String> listTwoStar;
    public static List<String> listThreeStar;
    public static List<String> listFourStar;
    public static List<String> listFiveStar;

    //MYSQL
    public static String username;
    public static String password;
    public static String port;
    public static String host;
    public static String database;

    //Other Stuff
    public static String WorldConfig;
    public static boolean Debug;
    public static String FoundChest;
    public static String FoundChest_NB;
    public static String BreakChest;
    public static int MaxCrates;
    public static double MaxSpawnTime;
    public static double MinSpawnTime;
    public static String lootcratesPrefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "LootCrates" + ChatColor.GRAY + "] ";

    //Max Distance Allowed
    public static int max_x;
    public static int min_x;
    public static int max_z;
    public static int min_z;

    //Item list
    public static ArrayList<Material> OneStar = new ArrayList<>();
    public static ArrayList<Material> TwoStar = new ArrayList<>();
    public static ArrayList<Material> ThreeStar = new ArrayList<>();
    public static ArrayList<Material> FourStar = new ArrayList<>();
    public static ArrayList<Material> FiveStar = new ArrayList<>();

    //Constructor | Retrieving everything from the Config.yml File
    public ConfigRetriever(main pl) {
        plugin = pl;

        //retrieving items from the config
        listOneStar = plugin.getConfig().getStringList("Items.One-Star");
        listTwoStar = plugin.getConfig().getStringList("Items.Two-Star");
        listThreeStar = plugin.getConfig().getStringList("Items.Three-Star");
        listFourStar = plugin.getConfig().getStringList("Items.Four-Star");
        listFiveStar = plugin.getConfig().getStringList("Items.Five-Star");

        //retrieving messages from the config
        FoundChest = plugin.getConfig().getString("Messages.Found-Chest");
        FoundChest_NB = plugin.getConfig().getString("Messages.Found-Chest-NotBreak");
        BreakChest = plugin.getConfig().getString("Messages.Break-Chest");

        //MYSQL config
        username = plugin.getConfig().getString("mysql.user");
        password = plugin.getConfig().getString("mysql.password");
        port = plugin.getConfig().getString("mysql.port");
        host = plugin.getConfig().getString("mysql.host");
        database = plugin.getConfig().getString("mysql.database");

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
        WorldConfig = plugin.getConfig().getString("Worlds");
        Debug = plugin.getConfig().getBoolean("Debug");
        MaxCrates = plugin.getConfig().getInt("Crates.max-amount");
        MaxSpawnTime = plugin.getConfig().getDouble("Crates.max-spawned-time");
        MinSpawnTime = plugin.getConfig().getDouble("Crates.min-spawned-time");

        //Max Distance Allowed
        max_x = plugin.getConfig().getInt("Crates.max-x-distance");
        min_x = plugin.getConfig().getInt("Crates.min-x-distance");
        max_z = plugin.getConfig().getInt("Crates.max-z-distance");
        min_z = plugin.getConfig().getInt("Crates.min-z-distance");
    }

    public static void checkMinAndMaxTimes(){

        if(MinSpawnTime > MaxSpawnTime){
            debugActive(true,"!!! Make sure the values in max-spawned-time and min-spawned-time are correct in the config !!!");
            disablePlugin();
        }

    }

}
