package co.neweden.LootCrates;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ConfigRetriever {
    private main plugin;

    public static List<String> listOneStar;
    public static List<String> listTwoStar;
    public static List<String> listThreeStar;
    public static List<String> listFourStar;
    public static List<String> listFiveStar;

    public static String WorldConfig;
    public static String FoundChest;
    public static int MaxCrates;
    public static double MaxSpawnTime;

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
        MaxCrates = plugin.getConfig().getInt("Crates.max-amount");
        MaxSpawnTime = plugin.getConfig().getDouble("Crates.max-spawned-time");
    }
}
