package co.neweden.LootCrates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

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
                " `number` INT(5) UNSIGNED NOT NULL AUTO_INCREMENT," +
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

    public static void initPlayerChestCount(UUID uuid){

        //get user data
        String sql = "INSERT INTO `loots` (`name`, `uuid`, `total_amount`, `one_star`, `two_star`, `three_star`, `four_star`, `five_star`) VALUES (?, ?, '0', '0', '0', '0', '0', '0')";
        String playerName = Bukkit.getPlayer(uuid).getDisplayName();
        try {
            PreparedStatement stmt = Main.con.prepareStatement(sql);
            stmt.setString(1, playerName);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
            Main.debugActive(false,"Added player: " + playerName + " to Database", null);
        } catch (SQLException e) {
            boolean chk = checkPlayerName(playerName, uuid);
            if (!chk) {
                Main.debugActive(false,"Duplicate player, not adding to Database", null);
            }
        }
    }

    private static boolean checkPlayerName(String name, UUID uuid){

        String sql = "UPDATE `loots` SET `name`= ? WHERE `uuid`= ?";
        boolean val = false;

        Loots loots = getPlayerLoots(uuid, null , "uuid");
        if(!loots.name.equalsIgnoreCase(name)){
            try {
                PreparedStatement stmt = Main.con.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();
                Main.debugActive(false,"Updated player name: " + name + " in Database", null);
                val =  true;
            } catch (SQLException e) {
                Main.debugActive(false,"Player name is the same", null);
            }
        }
        return val;
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

    public static void chestIsFound(UUID uuid,Block block) {

        ChestClass chClass = getCrateFromHashMap(block); //Checks if chest is found
        String name = Bukkit.getPlayer(uuid).getDisplayName();
        boolean correctCords = (chClass.x == block.getX() && chClass.y == block.getY() && chClass.z == block.getZ());
        boolean found = (!chClass.found);

        //Checks if the Coordinates are equal and if the Crate hasn't been found
        if (!correctCords) {
            return;
        }

        if (!found) {
            return;
        }

        Loots loots = getPlayerLoots(uuid, null , "uuid");

        ChestCount chCount = addPlayerChestCount(chClass.tier, loots.total, loots.one_star, loots.two_star, loots.three_star, loots.four_star, loots.five_star);

        //Mark the chest as found
        ChestClass chClass2 = new ChestClass();
        chClass2.world = chClass.world;
        chClass2.num = chClass.num;
        chClass2.x = chClass.x;
        chClass2.y = chClass.y;
        chClass2.z = chClass.z;
        chClass2.tier = chClass.tier;
        chClass2.found = true;
        cratesMap.put(block, chClass2);

        Main.debugActive(false, "Crate # " + chClass.num + ",Tier: " + chClass.tier + " was found by player: " + name + "", null);

        //Update current player number of chest found
        String sql = "UPDATE `loots` SET `total_amount` = ?, `one_star` = ?, `two_star` = ?, `three_star` = ?, `four_star` = ?, `five_star` = ? WHERE `loots`.`uuid` = ?";

        try {
            PreparedStatement stmt = Main.con.prepareStatement(sql);
            stmt.setInt(1, chCount.total);
            stmt.setInt(2, chCount.one_star);
            stmt.setInt(3, chCount.two_star);
            stmt.setInt(4, chCount.three_star);
            stmt.setInt(5, chCount.four_star);
            stmt.setInt(6, chCount.five_star);
            stmt.setString(7, uuid.toString());
            stmt.executeUpdate();
            Main.debugActive(false, "Updated player: " + name + " Crate data", null);
        } catch (SQLException e) {
            Main.debugActive(false, "Could not update player: " + name + " Crate data!!", e);
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

    private static int getChestCountInDb(){
        String sql = "SELECT COUNT(*) FROM `chests`";
        int count = 0;
        try {
            Statement stmt = Main.con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt("COUNT(*)");
            }
            return count;
        } catch (SQLException e) {
            Main.debugActive(false,"Could not retrieve current crates count from the Database !!", e);
            return count;
        }
    }

    public static ChestClass getChestFromCoords(Block block){
        String sql = "SELECT * FROM `chests` WHERE `x`=? AND `y`=? AND `z`=?";
        ChestClass chClass = new ChestClass();
        try {
            PreparedStatement stmt = Main.con.prepareStatement(sql);
            stmt.setInt(1, block.getX());
            stmt.setInt(2, block.getY());
            stmt.setInt(3, block.getZ());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                chClass.world = rs.getString("world");
                chClass.num = rs.getInt("number");
                chClass.x = rs.getInt("x");
                chClass.y = rs.getInt("y");
                chClass.z = rs.getInt("z");
                chClass.tier = rs.getInt("tier");
                chClass.found = rs.getBoolean("found");

            }
        } catch (SQLException e) {
            Main.debugActive(false, "Crate Could not be found !!", e);
        }

        return chClass;
    }

    static void loadCrates() {
        int count = getChestCountInDb();
        if (count == 0) {
            ChestSpawner.CreateChestOnStartup();
            return;
        }

        String sql = "SELECT * FROM `chests`";

        try {
            PreparedStatement stmt = Main.con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

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

    public static void removeCrateFromHashMap(Block block){
        cratesMap.remove(block);
    }

    public static void deleteChest(){
        for (Block block : new HashSet<>(cratesMap.keySet())) {
            Timer.DespawnChest(block, true);
        }
    }

    static void removeChestsFromDb(Block block) {

        ChestClass chClass = getChestFromCoords(block);
        String sql = "DELETE FROM `chests` WHERE number = ?";

        try {
            PreparedStatement stmt = Main.con.prepareStatement(sql);
            stmt.setInt(1, chClass.num);
            stmt.executeUpdate();
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