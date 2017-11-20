package co.neweden.LootCrates;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.UUID;

import static co.neweden.LootCrates.ConfigRetriever.*;
import static co.neweden.LootCrates.Main.con;
import static co.neweden.LootCrates.Main.debugActive;


public class Database {

    private static Connection connection = null;

    static Connection getConnection() throws SQLException {

        try{
            connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?autoReconnect=true", username, password);

            } catch(SQLException e){
            debugActive(true,"Database connection failed!! Please verify your MYSQL Config !!", e);
            }
        return connection;
    }

    static void initDatabase(){
        String sql = "CREATE TABLE IF NOT EXISTS `loots` (\n" +
                "    `name` VARCHAR(48) NOT NULL,\n" +
                "    `uuid` VARCHAR(48) NOT NULL,\n" +
                "    `total_amount` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `one_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `two_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `three_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `four_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `five_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    PRIMARY KEY (`uuid` (48))\n" +
                ")";

        String sql2 = "CREATE TABLE IF NOT EXISTS `chests` (\n" +
                "    `world` VARCHAR(25) NOT NULL,\n" +
                "    `number` INT(5) UNSIGNED NOT NULL,\n" +
                "    `x` INT(6) NOT NULL,\n" +
                "    `y` INT(3) NOT NULL,\n" +
                "    `z` INT(6) NOT NULL,\n" +
                "    `tier` TINYINT(5) UNSIGNED NOT NULL,\n" +
                "    `found` BOOLEAN NOT NULL DEFAULT FALSE,\n" +
                "\tUNIQUE (`number`)\n" +
                ");";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            debugActive(true,"Could not Create loots Table!! Please verify your MYSQL Config !!", e);
        }
        try {
            PreparedStatement stmt2 = con.prepareStatement(sql2);
            stmt2.executeUpdate();
            debugActive(false,"Database init() Verified", null);
        } catch (SQLException e) {
            debugActive(true,"Could not Create chests Table!! Please verify your MYSQL Config !!", e);
        }
    }

    static void addChestToDatabase(String w, int num, int x, int y, int z, int tier){

        String sql = "INSERT INTO `chests` (`world`, `number`, `x`, `y`, `z`, `tier`, `found`) VALUES (?, ?, ?, ?, ?, ?, '0')";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, w);
            stmt.setInt(2, num);
            stmt.setInt(3, x);
            stmt.setInt(4, y);
            stmt.setInt(5, z);
            stmt.setInt(6, tier);
            stmt.executeUpdate();
            debugActive(false,"Crate #" + num + " Added to Database", null);
        } catch (SQLException e) {
            debugActive(false,"Crate #" + num + " Could not be added !!", null);
        }
    }

    public static void initPlayerChestCount(UUID uuid){

        //get user data
        String sql = "INSERT INTO `loots` (`name`, `uuid`, `total_amount`, `one_star`, `two_star`, `three_star`, `four_star`, `five_star`) VALUES (?, ?, '0', '0', '0', '0', '0', '0')";
        String playerName = Bukkit.getPlayer(uuid).getDisplayName();
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, playerName);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
            debugActive(false,"Added player: " + playerName + " to Database", null);
        } catch (SQLException e) {
            boolean chk = checkPlayerName(playerName, uuid);
            if (!chk) {
                debugActive(false,"Duplicate player, not adding to Database", null);
            }
        }
    }

    private static boolean checkPlayerName(String name, UUID uuid){

        String sql = "UPDATE `loots` SET `name`= ? WHERE `uuid`= ?";
        boolean val = false;

        Loots loots = getPlayerLoots(uuid);
        if(!loots.name.equalsIgnoreCase(name)){
            try {
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();
                debugActive(false,"Updated player name: " + name + " in Database", null);
                val =  true;
            } catch (SQLException e) {
                //debugActive(false,"Player name is the same", null);
            }
        }
        return val;
    }

    public static Loots getPlayerLoots(UUID uuid){

        String sql = "SELECT * FROM `loots` WHERE `uuid` = ?";

        Loots loots = new Loots();
        loots.uuid = uuid;

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                loots.name = rs.getString("name");
                loots.total = rs.getInt("total_amount");
                loots.one_star = rs.getInt("one_star");
                loots.two_star = rs.getInt("two_star");
                loots.three_star = rs.getInt("three_star");
                loots.four_star = rs.getInt("four_star");
                loots.five_star = rs.getInt("five_star");

            }
        } catch (SQLException e) {
            debugActive(false,"Could not find anything for this player: " + uuid.toString(), null);
        }
        return loots;
    }

    public static ChestNumberReturn chestNumberReturn(int x, int y, int z){

            ChestNumberReturn chNum = new ChestNumberReturn();

            String sql = "SELECT number FROM chests WHERE (`x` = ? AND `y` = ? AND `z` = ?) GROUP BY number";

            try {
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setInt(1, x);
                stmt.setInt(2, y);
                stmt.setInt(3, z);
                ResultSet rs = stmt.executeQuery();

                // iterate through the java resultset
                while (rs.next()) {
                    chNum.num = rs.getInt("number");
                    chNum.value = 1;
                }
            } catch (SQLException e) {
                chNum.value = 0;
                debugActive(false,"Reported Chest was not found", null);
            }
            return chNum;
    }

    public static ChestFromNum getChestFromNum(int num){
        ChestFromNum chFNum = new ChestFromNum();

        String sql = "SELECT * FROM `chests` WHERE `number` = ?";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, num);
            ResultSet rs = stmt.executeQuery();

            // iterate through the java resultset
            while (rs.next()) {
                chFNum.num = num;
                chFNum.x = rs.getInt("x");
                chFNum.y = rs.getInt("y");
                chFNum.z = rs.getInt("z");
                chFNum.tier = rs.getInt("tier");
                chFNum.found = rs.getInt("found");
                chFNum.exist = 1;

            }
        } catch (SQLException e) {
            chFNum.exist = 0;
            debugActive(false,"Could not retrieve crate # " + num + " data!!", null);
        }
        return chFNum;
    }

    public static void chestIsFound(UUID uuid, int x, int y, int z){

        ChestNumberReturn chNum = chestNumberReturn(x, y, z);
        String name = Bukkit.getPlayer(uuid).getDisplayName();
        ChestFromNum chFNum = getChestFromNum(chNum.num);
        boolean correctCords = (chFNum.x == x && chFNum.y == y && chFNum.z == z);
        boolean found = (chFNum.found == 0);

        //Checks if the Coordinates are equal and if the Crate hasn't been found
        if(!correctCords){return;}
        else{
            if(!found){return;}
            else{

                Loots loots = getPlayerLoots(uuid);

                ChestCount chCount = addPlayerChestCount(chFNum.tier,loots.total,loots.one_star,loots.two_star,loots.three_star,loots.four_star,loots.five_star);

                //Mark the chest as found
                String sql3 = "UPDATE `chests` SET `found` = '1' WHERE `number` = ?";

                try {
                    PreparedStatement stmt = con.prepareStatement(sql3);
                    stmt.setInt(1, chNum.num);
                    stmt.executeUpdate();
                    debugActive(false,"Crate # " + chNum.num + ",Tier: " + chFNum.tier + " was found by player: " + name + "", null);
                } catch (SQLException e) {
                    debugActive(false,"Could not update crate found by: " + name + " !!", null);
                }

                //Update current player number of chest found
                String sql4 = "UPDATE `loots` SET `total_amount` = ?, `one_star` = ?, `two_star` = ?, `three_star` = ?, `four_star` = ?, `five_star` = ? WHERE `loots`.`uuid` = ?";

                try {
                    PreparedStatement stmt = con.prepareStatement(sql4);
                    stmt.setInt(1, chCount.total);
                    stmt.setInt(2, chCount.one_star);
                    stmt.setInt(3, chCount.two_star);
                    stmt.setInt(4, chCount.three_star);
                    stmt.setInt(5, chCount.four_star);
                    stmt.setInt(6, chCount.five_star);
                    stmt.setString(7, uuid.toString());
                    stmt.executeUpdate();
                    debugActive(false,"Updated player: " + name + " Crate data", null);
                } catch (SQLException e) {
                    debugActive(false,"Could not update player: " + name + " Crate data!!", null);
                }
            }
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

    static void removeChestsFromDb() {
        String sql = "TRUNCATE TABLE `chests`";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            debugActive(false,"Crates cleared from Database", null);
        } catch (SQLException e) {
            debugActive(false,"Could not remove all the crates from the Database !!", e);
        }
    }

    public static void removeChestEvent(int x, int y, int z) {

        ChestNumberReturn chNum = chestNumberReturn(x, y, z);
        String sql = "DELETE FROM `chests` WHERE number = ?";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, chNum.num);
            stmt.executeUpdate();
            debugActive(false, "Crate removed from Database", null);
        } catch (SQLException e) {
            debugActive(false, "Could not remove the crates from the Database !!", null);
        }
    }

    public static int getCurrentChestsCount(){
        String sql = "SELECT COUNT(*) FROM `chests`";
        int count = 0;
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt("COUNT(*)");
            }
            return count;
        } catch (SQLException e) {
            debugActive(false,"Could not retrieve current crates count from the Database !!", null);
            return count;
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

    public static class ChestNumberReturn {
        public int num;
        public int value;
    }

    public static class ChestFromNum {
        public int num;
        int x;
        int y;
        int z;
        public int tier;
        public int found; //0 = false; 1 = true;
        int exist;
    }
}
