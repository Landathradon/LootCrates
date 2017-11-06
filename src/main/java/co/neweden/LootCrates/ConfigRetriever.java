package co.neweden.LootCrates;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ConfigRetriever {
    private main plugin;

    public static List<String> listCommon;
    public static List<String> listUncommon;
    public static List<String> listRare;
    public static List<String> CommonMsg;
    public static List<String> UncommonMsg;
    public static List<String> RareMsg;

    public static String WorldConfig;
    public static int MaxCrates;
    public static int MaxSpawnTime;

    public static ArrayList<Material> common = new ArrayList<>();
    public static ArrayList<Material> uncommon = new ArrayList<>();
    public static ArrayList<Material> rare = new ArrayList<>();

    //Constructor | Retrieving everything from the Config.yml File
    public ConfigRetriever(main pl) {
        plugin = pl;

        //retrieving items from the config
        listCommon = plugin.getConfig().getStringList("Items.common");
        listUncommon = plugin.getConfig().getStringList("Items.uncommon");
        listRare = plugin.getConfig().getStringList("Items.rare");

        //retrieving messages from the config
        CommonMsg = plugin.getConfig().getStringList("Messages.common");
        UncommonMsg = plugin.getConfig().getStringList("Messages.uncommon");
        RareMsg = plugin.getConfig().getStringList("Messages.rare");

        //adding the items to their rarity list
        ////One Star Crate List
        common.add(Material.getMaterial(listCommon.get(0)));
        common.add(Material.getMaterial(listCommon.get(1)));

        //Two Star Crate List
        uncommon.add(Material.getMaterial(listUncommon.get(0)));
        uncommon.add(Material.getMaterial(listUncommon.get(1)));

        //Three Star Crate List
        rare.add(Material.getMaterial(listRare.get(0)));
        rare.add(Material.getMaterial(listRare.get(1)));

        WorldConfig = plugin.getConfig().getString("Worlds");
        MaxCrates = plugin.getConfig().getInt("Crates.max-amount");
        MaxSpawnTime = plugin.getConfig().getInt("Crates.max-spawned-time");
    }
}
