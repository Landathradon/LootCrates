package co.neweden.LootCrates;

import java.sql.*;

import static co.neweden.LootCrates.ConfigRetriever.*;
import static co.neweden.LootCrates.main.plugin;


public class Database {



    public static Connection getConnection() throws SQLException {

                Connection conn = null;
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?autoReconnect=true&useSSL=false", username, password);

                }catch(SQLException se){
                    //se.printStackTrace();
                    plugin.getLogger().info("Database connection failed!! Please verify your MYSQL Config !!");
                }catch(Exception e){
                    //e.printStackTrace();
                    plugin.getLogger().info("Database connection failed!! Please verify your MYSQL Config !!");
                }
                return conn;
    }

    public static void initDatabase(){
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
                "\tUNIQUE (`number`)\n" +
                ");";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
            plugin.getLogger().info("Could not Create loots Table!! Please verify your MYSQL Config !!");
        }
        try {
            PreparedStatement stmt2 = getConnection().prepareStatement(sql2);
            stmt2.executeUpdate();
            plugin.getLogger().info("Database init() Verified");
        } catch (SQLException e) {
            //e.printStackTrace();
            plugin.getLogger().info("Could not Create chests Table!! Please verify your MYSQL Config !!");
        }
    }

    public static void addChestToDatabase(String w, int num, int x, int y, int z, int tier){

    String sql = "INSERT INTO `chests` (`world`, `number`, `x`, `y`, `z`, `tier`, `found`) VALUES ('" + w + "', '" + num + "', '" + x + "', '" + y + "', '" + z + "', '" + tier + "', '0')";

        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.executeUpdate();
            plugin.getLogger().info("Chest #" + num + " Added to Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            plugin.getLogger().info("Chest #" + num + " Could not be added !!");
        }


    }

    public static void initPlayerChestCount(String name){

        //get user data
        String sql = "INSERT INTO `loots` (`name`, `total_amount`, `one_star`, `two_star`, `three_star`, `four_star`, `five_star`) VALUES ('" + name + "', '0', '0', '0', '0', '0', '0')";

        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.executeUpdate();
            plugin.getLogger().info("Added player: " + name + " to Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            plugin.getLogger().info("Duplicate player, not adding");
        }

    }

    public static int[] getPlayerLoots(String name){

        String sql = "SELECT * FROM `loots` WHERE `name`='" +  name + "'";

        int sqltot = 0;
        int sqlone = 0;
        int sqltwo = 0;
        int sqlthree = 0;
        int sqlfour = 0;
        int sqlfive = 0;

        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // iterate through the java resultset
            while (rs.next()) {

                sqltot = rs.getInt("total_amount");
                sqlone = rs.getInt("one_star");
                sqltwo = rs.getInt("two_star");
                sqlthree = rs.getInt("three_star");
                sqlfour = rs.getInt("four_star");
                sqlfive = rs.getInt("five_star");

            }
            stmt.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        int ar[] = new int[6];
        ar[0] = sqltot;
        ar[1] = sqlone;
        ar[2] = sqltwo;
        ar[3] = sqlthree;
        ar[4] = sqlfour;
        ar[5] = sqlfive;

        return ar;

    }

    public static int chestNumberReturn(int x, int y, int z){

        int sqlnm = 0;

        String sql = "SELECT number FROM chests WHERE (`x` = '" + x + "' AND `y` = '" + y + "' AND `z` = '" + z + "') GROUP BY number HAVING COUNT(DISTINCT x)=1";

        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // iterate through the java resultset
            while (rs.next()) {
                sqlnm = rs.getInt("number");
            }
            stmt.close();
        } catch (SQLException e1) {
            plugin.getLogger().info("Reported Chest was not found");
        }
        return sqlnm;
    }

    public static void chestIsFound(String name, int x, int y, int z){

        int sqlnm = chestNumberReturn(x, y, z);
        int sqlx = 0;
        int sqly = 0;
        int sqlz = 0;
        int sqltier = 0;
        int sqlfound = 0; //0 = false; 1 = true;


        String sql2 = "SELECT * FROM `chests` WHERE `number` = '" + sqlnm + "'";

        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql2);

            // iterate through the java resultset
            while (rs.next()) {

                sqlx = rs.getInt("x");
                sqly = rs.getInt("y");
                sqlz = rs.getInt("z");
                sqltier = rs.getInt("tier");
                sqlfound = rs.getInt("found");

            }
            stmt.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
            plugin.getLogger().info("Could not retrieve chest# " + sqlnm + " data!!");
        }

        //Checks if the Coordinates are equal and if the Crate hasn't been found
        if(sqlx == x && sqly == y && sqlz == z){
            if(sqlfound == 0){

                int ar[] = getPlayerLoots(name);

                int total = ar[0];
                int one_star = ar[1];
                int two_star = ar[2];
                int three_star = ar[3];
                int four_star = ar[4];
                int five_star = ar[5];

                int ar2[] = addPlayerChestCount(sqltier,total,one_star,two_star,three_star,four_star,five_star);

                int newtot = ar2[0];
                int newone = ar2[1];
                int newtwo = ar2[2];
                int newthree = ar2[3];
                int newfour = ar2[4];
                int newfive = ar2[5];

                //Mark the chest as found
                String sql3 = "UPDATE `chests` SET `found` = '1' WHERE `number` = '" + sqlnm + "'";

                try {
                    PreparedStatement stmt = getConnection().prepareStatement(sql3);
                    stmt.executeUpdate();
                    plugin.getLogger().info("Chest # " + sqlnm + ",Tier: " + sqltier + " was found by player: " + name + "");
                } catch (SQLException e) {
                    //e.printStackTrace();
                    plugin.getLogger().info("Could not update chest found by: " + name + " !!");
                }

                //Update current player number of chest found
                String sql4 = "UPDATE `loots` SET `total_amount` = '" + newtot + "', `one_star` = '" + newone + "', `two_star` = '" + newtwo + "', `three_star` = '" + newthree + "', `four_star` = '" + newfour + "', `five_star` = '" + newfive + "' WHERE `loots`.`name` = '" +  name + "'";

                try {
                    PreparedStatement stmt = getConnection().prepareStatement(sql4);
                    stmt.executeUpdate();
                    plugin.getLogger().info("Updated player: " + name + " Chest data");
                } catch (SQLException e) {
                    //e.printStackTrace();
                    plugin.getLogger().info("Could not update player: " + name + " Chest data!!");
                }

            }

        }

    }

    public static int[] addPlayerChestCount(int tier, int tot, int one, int two, int three, int four, int five){

        int caltot = tot;
        int calone = 0;
        int caltwo = 0;
        int calthree = 0;
        int calfour = 0;
        int calfive = 0;

        if(tier == 1){caltot++;calone = one + 1;}
        if(tier == 2){caltot++;caltwo = two + 1;}
        if(tier == 3){caltot++;calthree = three + 1;}
        if(tier == 4){caltot++;calfour = four + 1;}
        if(tier == 5){caltot++;calfive = five + 1;}

        int ar[] = new int[6];
        ar[0] = caltot;
        ar[1] = calone;
        ar[2] = caltwo;
        ar[3] = calthree;
        ar[4] = calfour;
        ar[5] = calfive;
        return ar;
    }

    public static void retrieveChest(){

        // Retrieve all chest coords and store it in an array

    }

    public static void removeChests() {
        String sql = "TRUNCATE TABLE `chests`";

        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.executeUpdate();
            plugin.getLogger().info("Chests cleared from Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            plugin.getLogger().info("Could not remove all the chests from the Database !!");
        }
    }

    public static void removeChestEvent(int x, int y, int z) {

       int num = chestNumberReturn(x, y, z);

        String sql = "DELETE FROM `chests` WHERE number = '" + num + "';";

        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.executeUpdate();
            plugin.getLogger().info("Chest cleared from Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            plugin.getLogger().info("Could not remove the chest from the Database !!");
        }
    }
}