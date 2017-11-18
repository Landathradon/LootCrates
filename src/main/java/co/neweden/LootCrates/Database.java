package co.neweden.LootCrates;

import java.sql.*;

import static co.neweden.LootCrates.ConfigRetriever.*;
import static co.neweden.LootCrates.main.con;
import static co.neweden.LootCrates.main.debugActive;


public class Database {

    private static Connection connection = null;

    static Connection getConnection() throws SQLException {

        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?autoReconnect=true", username, password);

            } catch(Exception e){
                 //e.printStackTrace();
            debugActive(true,"Database connection failed!! Please verify your MYSQL Config !!");
            }
        return connection;
    }

    static void initDatabase(){
        String sql = "CREATE TABLE IF NOT EXISTS `loots` (\n" +
                "    `name` VARCHAR(48) NOT NULL,\n" +
                "    `total_amount` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `one_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `two_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `three_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `four_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    `five_star` INT(10) UNSIGNED NOT NULL DEFAULT '0',\n" +
                "    PRIMARY KEY (`name` (48))\n" +
                ")";

        String sql2 = "CREATE TABLE IF NOT EXISTS `chests` (\n" +
                "    `world` VARCHAR(25) NOT NULL,\n" +
                "    `number` INT(5) UNSIGNED NOT NULL,\n" +
                "    `x` INT(6) NOT NULL,\n" +
                "    `y` INT(3) NOT NULL,\n" +
                "    `z` INT(6) NOT NULL,\n" +
                "    `tier` TINYINT(5) UNSIGNED NOT NULL,\n" +
                "    `found` BOOLEAN NOT NULL DEFAULT FALSE,\n" +
                "    `opened` BOOLEAN NOT NULL DEFAULT FALSE,\n" +
                "\tUNIQUE (`number`)\n" +
                ");";
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(true,"Could not Create loots Table!! Please verify your MYSQL Config !!");
        }
        try {
            PreparedStatement stmt2 = con.prepareStatement(sql2);
            stmt2.executeUpdate();
            debugActive(false,"Database init() Verified");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(true,"Could not Create chests Table!! Please verify your MYSQL Config !!");
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
            debugActive(false,"Crate #" + num + " Added to Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(false,"Crate #" + num + " Could not be added !!");
        }
    }

    public static void initPlayerChestCount(String name){

        //get user data
        String sql = "INSERT INTO `loots` (`name`, `total_amount`, `one_star`, `two_star`, `three_star`, `four_star`, `five_star`) VALUES (?, '0', '0', '0', '0', '0', '0')";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.executeUpdate();
            debugActive(false,"Added player: " + name + " to Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(false,"Duplicate player, not adding");
        }

    }

    public static int[] getPlayerLoots(String name){

        String sql = "SELECT * FROM `loots` WHERE `name`='" +  name + "'";

        int sqltot;
        int sqlone;
        int sqltwo;
        int sqlthree;
        int sqlfour;
        int sqlfive;
        int ar[] = new int[6];

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // iterate through the java resultset
            while (rs.next()) {

                sqltot = rs.getInt("total_amount");
                sqlone = rs.getInt("one_star");
                sqltwo = rs.getInt("two_star");
                sqlthree = rs.getInt("three_star");
                sqlfour = rs.getInt("four_star");
                sqlfive = rs.getInt("five_star");

                ar[0] = sqltot;
                ar[1] = sqlone;
                ar[2] = sqltwo;
                ar[3] = sqlthree;
                ar[4] = sqlfour;
                ar[5] = sqlfive;
            }
        } catch (SQLException e1) {
            debugActive(false,"Could not find anything for this player: " + name);
        }
        return ar;
    }

    public static int[] chestNumberReturn(int x, int y, int z){

            int sql_nm;
            int ar[] = new int[2];

            String sql = "SELECT number FROM chests WHERE (`x` = '" + x + "' AND `y` = '" + y + "' AND `z` = '" + z + "') GROUP BY number";

            try {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                // iterate through the java resultset
                while (rs.next()) {
                    sql_nm = rs.getInt("number");

                    ar[0] = sql_nm;
                    ar[1] = 1;
                }
            } catch (SQLException e1) {
                ar[1] = 0;
                //debugActive(false,"Reported Chest was not found");
            }
            return ar;
    }

    public static int[] getChestFromNum(int num){
        int ar[] = new int[8];
        int sql_x;
        int sql_y;
        int sql_z;
        int sql_tier;
        int sql_found; //0 = false; 1 = true;
        int sql_opened; //0 = false; 1 = true;

        String sql2 = "SELECT * FROM `chests` WHERE `number` = '" + num + "'";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql2);

            // iterate through the java resultset
            while (rs.next()) {

                sql_x = rs.getInt("x");
                sql_y = rs.getInt("y");
                sql_z = rs.getInt("z");
                sql_tier = rs.getInt("tier");
                sql_found = rs.getInt("found");
                sql_opened = rs.getInt("opened");

                ar[0] = num;
                ar[1] = sql_x;
                ar[2] = sql_y;
                ar[3] = sql_z;
                ar[4] = sql_tier;
                ar[5] = sql_found;
                ar[6] = sql_opened;
                ar[7] = 1;

            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            ar[7] = 0;
            debugActive(false,"Could not retrieve crate # " + num + " data!!");
        }
        return ar;
    }

    public static void chestIsFound(String name, int x, int y, int z){

        int[] value = chestNumberReturn(x, y, z);
        int sql_nm = value[0];
        int[] chest = getChestFromNum(sql_nm);
        int sql_x = chest[1];
        int sql_y = chest[2];
        int sql_z = chest[3];
        int sql_tier = chest[4];
        int sql_found = chest[5]; //0 = false; 1 = true;

        //Checks if the Coordinates are equal and if the Crate hasn't been found
        if(sql_x == x && sql_y == y && sql_z == z){
            if(sql_found == 0){

                int ar[] = getPlayerLoots(name);

                int total = ar[0];
                int one_star = ar[1];
                int two_star = ar[2];
                int three_star = ar[3];
                int four_star = ar[4];
                int five_star = ar[5];

                int ar2[] = addPlayerChestCount(sql_tier,total,one_star,two_star,three_star,four_star,five_star);

                int new_tot = ar2[0];
                int new_one = ar2[1];
                int new_two = ar2[2];
                int new_three = ar2[3];
                int new_four = ar2[4];
                int new_five = ar2[5];

                //Mark the chest as found
                String sql3 = "UPDATE `chests` SET `found` = '1' WHERE `number` = ?";

                try {
                    PreparedStatement stmt = con.prepareStatement(sql3);
                    stmt.setInt(1, sql_nm);
                    stmt.executeUpdate();
                    debugActive(false,"Crate # " + sql_nm + ",Tier: " + sql_tier + " was found by player: " + name + "");
                } catch (SQLException e) {
                    //e.printStackTrace();
                    debugActive(false,"Could not update crate found by: " + name + " !!");
                }

                //Update current player number of chest found
                String sql4 = "UPDATE `loots` SET `total_amount` = ?, `one_star` = ?, `two_star` = ?, `three_star` = ?, `four_star` = ?, `five_star` = ? WHERE `loots`.`name` = ?";

                try {
                    PreparedStatement stmt = con.prepareStatement(sql4);
                    stmt.setInt(1, new_tot);
                    stmt.setInt(2, new_one);
                    stmt.setInt(3, new_two);
                    stmt.setInt(4, new_three);
                    stmt.setInt(5, new_four);
                    stmt.setInt(6, new_five);
                    stmt.setString(7, name);
                    stmt.executeUpdate();
                    debugActive(false,"Updated player: " + name + " Crate data");
                } catch (SQLException e) {
                    //e.printStackTrace();
                    debugActive(false,"Could not update player: " + name + " Crate data!!");
                }
            }
        }
    }

    private static int[] addPlayerChestCount(int tier, int tot, int one, int two, int three, int four, int five){

        int cal_tot = tot;
        int cal_one = one;
        int cal_two = two;
        int cal_three = three;
        int cal_four = four;
        int cal_five = five;

        if(tier == 1){cal_tot++;cal_one = one + 1;}
        if(tier == 2){cal_tot++;cal_two = two + 1;}
        if(tier == 3){cal_tot++;cal_three = three + 1;}
        if(tier == 4){cal_tot++;cal_four = four + 1;}
        if(tier == 5){cal_tot++;cal_five = five + 1;}

        int ar[] = new int[6];
        ar[0] = cal_tot;
        ar[1] = cal_one;
        ar[2] = cal_two;
        ar[3] = cal_three;
        ar[4] = cal_four;
        ar[5] = cal_five;
        return ar;
    }

    public static void chestHasOpen(int num){

        //Mark the chest as open
        String sql = "UPDATE `chests` SET `opened` = '1' WHERE `number` = ?";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, num);
            stmt.executeUpdate();
            debugActive(false,"Crate # " + num + ", was opened");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(false,"Could not update crate # " + num + " !!");
        }
    }

    static void removeChestsFromDb() {
        String sql = "TRUNCATE TABLE `chests`";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            debugActive(false,"Crates cleared from Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(false,"Could not remove all the crates from the Database !!");
        }
    }

    public static void removeChestEvent(int x, int y, int z) {

        int[] num = chestNumberReturn(x, y, z);
        String sql = "DELETE FROM `chests` WHERE number = ?";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, num[0]);
            stmt.executeUpdate();
            debugActive(false, "Crate removed from Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(false, "Could not remove the crates from the Database !!");
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
            debugActive(false,"Could not retrieve current crates count from the Database !!");
            return count;
        }
    }
}