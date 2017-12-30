package co.neweden.LootCrates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class Database {

    private static Connection connection = null;
    static Map<Block, ChestClass> cratesMap = new HashMap<>();

    static Connection getConnection() throws SQLException {

        try{
            connection = DriverManager.getConnection("jdbc:mysql://"+ConfigRetriever.host+":"+ConfigRetriever.port+"/"+ConfigRetriever.database+"?autoReconnect=true", ConfigRetriever.username, ConfigRetriever.password);

            } catch(SQLException e){
            Main.debugActive(true,"Database connection failed!! Please verify your MYSQL Config !!", e);
            }
        return connection;
    }

    static void initDatabase(){
        String sql = "CREATE TABLE IF NOT EXISTS `loots` (" +
                "    `name` VARCHAR(48) NOT NULL," +
                "    `uuid` VARCHAR(48) NOT NULL," +
                "    `total_amount` INT(10) UNSIGNED NOT NULL DEFAULT '0'," +
                "    `one_star` INT(10) UNSIGNED NOT NULL DEFAULT '0'," +
                "    `two_star` INT(10) UNSIGNED NOT NULL DEFAULT '0'," +
                "    `three_star` INT(10) UNSIGNED NOT NULL DEFAULT '0'," +
                "    `four_star` INT(10) UNSIGNED NOT NULL DEFAULT '0'," +
                "    `five_star` INT(10) UNSIGNED NOT NULL DEFAULT '0'," +
                "    PRIMARY KEY (`uuid` (48)))";

        String sql2 = "CREATE TABLE IF NOT EXISTS `chests`(" +
                " `world` VARCHAR(25) NOT NULL," +
                " `number` INT(5) UNSIGNED AUTO_INCREMENT," +
                " `x` INT(6) NOT NULL," +
                " `y` INT(3) NOT NULL," +
                " `z` INT(6) NOT NULL," +
                " `tier` TINYINT(5) UNSIGNED NOT NULL," +
                " `found` BOOLEAN NOT NULL DEFAULT FALSE," +
                " PRIMARY KEY (`number`))";
        try {
            PreparedStatement stmt = Main.con.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Main.debugActive(true,"Could not Create loots Table!! Please verify your MYSQL Config !!", e);
        }
        try {
            PreparedStatement stmt2 = Main.con.prepareStatement(sql2);
            stmt2.executeUpdate();
            Main.debugActive(false,"Database init() Verified", null);
        } catch (SQLException e) {
            Main.debugActive(true,"Could not Create chests Table!! Please verify your MYSQL Config !!", e);
        }
    }

    static void addChestToDatabase(String w, Block block, int tier){

            String sql = "INSERT INTO `chests` (`world`, `x`, `y`, `z`, `tier`, `found`) VALUES (?, ?, ?, ?, ?, ?)";

            try {
                PreparedStatement stmt = Main.con.prepareStatement(sql);
                stmt.setString(1, w);
                stmt.setInt(2, block.getX());
                stmt.setInt(3, block.getY());
                stmt.setInt(4, block.getZ());
                stmt.setInt(5, tier);
                stmt.setBoolean(6, false);
                stmt.executeUpdate();
                Main.debugActive(false, "Crate Added to Database", null);
            } catch (SQLException e) {
                Main.debugActive(false, "Crate Could not be added !!", e);
            }
    }

    public static Loots getPlayerLoots(UUID uuid, String name, String type) {

        Loots loots = new Loots();

        String sql = "";
        String forValue = "";
        if (type.equals("uuid")) {
            sql = "SELECT * FROM `loots` WHERE `uuid` = ?";
            forValue = uuid.toString();
        } else if (type.equals("name")) {
            sql = "SELECT * FROM `loots` WHERE `name` = ?";
            forValue = name;
        }

            try {
                PreparedStatement stmt = Main.con.prepareStatement(sql);
                stmt.setString(1, forValue);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    loots.uuid = UUID.fromString(rs.getString("uuid"));
                    loots.name = rs.getString("name");
                    loots.total = rs.getInt("total_amount");
                    loots.one_star = rs.getInt("one_star");
                    loots.two_star = rs.getInt("two_star");
                    loots.three_star = rs.getInt("three_star");
                    loots.four_star = rs.getInt("four_star");
                    loots.five_star = rs.getInt("five_star");
                }
            } catch (SQLException e) {
                Main.debugActive(false, "Could not find anything for this player: " + forValue, e);
            }

        return loots;
    }

    public static void chestIsFound(Player player, ChestClass chest) {

        if (chest.found) return;

        Loots loots = getPlayerLoots(player.getUniqueId(), null , "uuid");

        ChestCount chCount = addPlayerChestCount(chest.tier, loots.total, loots.one_star, loots.two_star, loots.three_star, loots.four_star, loots.five_star);

        //Mark the chest as found
        chest.found = true;

        Main.debugActive(false, "Crate # " + chest.num + ", Tier: " + chest.tier + " was found by player: " + player.getDisplayName() + "", null);

        //Count the First new chest
        int one = 0;
        int two = 0;
        int three = 0;
        int four = 0;
        int five = 0;

        if(chest.tier == 1){one++;}
        if(chest.tier == 2){two++;}
        if(chest.tier == 3){three++;}
        if(chest.tier == 4){four++;}
        if(chest.tier == 5){five++;}

        //Update current player number of chest found
        String sql =
                "INSERT INTO `loots` (`name`, `uuid`, `total_amount`, `one_star`, `two_star`, `three_star`, `four_star`, `five_star`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)" +
                " ON DUPLICATE KEY " +
                "UPDATE `loots` SET `total_amount` = ?, `one_star` = ?, `two_star` = ?, `three_star` = ?, `four_star` = ?, `five_star` = ? WHERE `uuid` = ?";
        try {
            PreparedStatement stmt = Main.con.prepareStatement(sql);
            stmt.setString(1, player.getDisplayName());
            stmt.setString(2, player.getUniqueId().toString());
            stmt.setInt(3, 1);
            stmt.setInt(4, one);
            stmt.setInt(5, two);
            stmt.setInt(6, three);
            stmt.setInt(7, four);
            stmt.setInt(8, five);
            stmt.setInt(9, chCount.total);
            stmt.setInt(10, chCount.one_star);
            stmt.setInt(11, chCount.two_star);
            stmt.setInt(12, chCount.three_star);
            stmt.setInt(13, chCount.four_star);
            stmt.setInt(14, chCount.five_star);
            stmt.setString(15, player.getUniqueId().toString());
            stmt.executeUpdate();
            Main.debugActive(false, "Updated player: " + player.getDisplayName() + " Crate data", null);
        } catch (SQLException e) {
            Main.debugActive(false, "Could not update player: " + player.getDisplayName() + " Crate data!!", e);
        }
    }

    private static ChestCount addPlayerChestCount(int tier, int tot, int one, int two, int three, int four, int five){

        ChestCount chCount = new ChestCount();
        chCount.total = tot;
        chCount.one_star = one;
        chCount.two_star = two;
        chCount.three_star = three;
        chCount.four_star = four;
        chCount.five_star = five;

        if(tier == 1){chCount.total++;chCount.one_star++;}
        if(tier == 2){chCount.total++;chCount.two_star++;}
        if(tier == 3){chCount.total++;chCount.three_star++;}
        if(tier == 4){chCount.total++;chCount.four_star++;}
        if(tier == 5){chCount.total++;chCount.five_star++;}

        return chCount;
    }

    public static int getCurrentChestsCount(){
        return cratesMap.size();
    }

    static void loadCrates() {
        String sql = "SELECT * FROM `chests`";

        try {
            PreparedStatement stmt = Main.con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                ChestSpawner.CreateChestOnStartup();
                return;
            }

            while (rs.next()) {
                ChestClass chClass = new ChestClass();
                chClass.world = rs.getString("world");
                chClass.num = rs.getInt("number");
                chClass.x = rs.getInt("x");
                chClass.y = rs.getInt("y");
                chClass.z = rs.getInt("z");
                chClass.tier = rs.getInt("tier");
                chClass.found = rs.getBoolean("found");

                World world = Bukkit.getWorld(chClass.world);
                if (world == null) continue;
                Block block = new Location(world, chClass.x, chClass.y, chClass.z).getBlock();
                Main.debugActive(false, "A Special Chest #" + chClass.num + ", Tier " + chClass.tier + " was spawned", null);
                cratesMap.put(block, chClass);
                if(chClass.found){
                    Timer.OnCrateCreated(block, 6000); //6000=5min, 600=30sec
                }else {
                    Timer.OnCrateCreated(block);
                }
            }
        } catch (SQLException e) {
            Main.debugActive(false, "Could not retrieve crate data !!!", e);
        }
    }

    public static ChestClass getCrateFromHashMap(Block block) {
        return cratesMap.get(block);
    }

    public static void deleteChest(){
        for (Block block : new HashSet<>(cratesMap.keySet())) {
            Timer.DespawnChest(block, true);
        }
    }

    public static void removeChest(Block block) {
        ChestClass chClass = getCrateFromHashMap(block);
        String sql = "DELETE FROM `chests` WHERE `x` = ? AND `y` = ? AND `z` = ?";

        try {
            PreparedStatement stmt = Main.con.prepareStatement(sql);
            stmt.setInt(1, chClass.x);
            stmt.setInt(2, chClass.y);
            stmt.setInt(3, chClass.z);
            stmt.executeUpdate();
            cratesMap.remove(block);
            Main.debugActive(false, "Crate removed from Database", null);
        } catch (SQLException e) {
            Main.debugActive(false, "Could not remove the crates from the Database !!", e);
        }
    }

    public static class Loots {
        String name;
        UUID uuid;
        public int total;
        public int one_star;
        public int two_star;
        public int three_star;
        public int four_star;
        public int five_star;
    }

    public static class ChestCount {
        int total;
        int one_star;
        int two_star;
        int three_star;
        int four_star;
        int five_star;
    }

    public static class ChestClass{
        String world;
        private int num;
        int x;
        int y;
        int z;
        public int tier;
        public boolean found; //0 = false; 1 = true;
    }
}