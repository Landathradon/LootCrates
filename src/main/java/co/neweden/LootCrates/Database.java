package co.neweden.LootCrates;

import java.sql.*;

import static co.neweden.LootCrates.ConfigRetriever.*;
import static co.neweden.LootCrates.Timer.DespawnChest;
import static co.neweden.LootCrates.main.con;
import static co.neweden.LootCrates.main.debugActive;


public class Database {

    static Connection connection = null;

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
            debugActive(true,"Database init() Verified");
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
            debugActive(false,"Chest #" + num + " Added to Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(false,"Chest #" + num + " Could not be added !!");
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

    static int[] getPlayerLoots(String name){

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
            e1.printStackTrace();
        }


        return ar;

    }

    public static int[] chestNumberReturn(int x, int y, int z){

            int sqlnm = 0;
            int ar[] = new int[2];

            String sql = "SELECT number FROM chests WHERE (`x` = '" + x + "' AND `y` = '" + y + "' AND `z` = '" + z + "') GROUP BY number HAVING COUNT(DISTINCT x)=1";

            try {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                // iterate through the java resultset
                while (rs.next()) {
                    sqlnm = rs.getInt("number");

                    ar[0] = sqlnm;
                    ar[1] = 1;
                }
            } catch (SQLException e1) {
                ar[1] = 0;
                debugActive(false,"Reported Chest was not found");
            }

            return ar;

    }

    public static int[] getChestFromNum(int num){
        int ar[] = new int[8];
        int sqlnm = num;
        int sqlx;
        int sqly;
        int sqlz;
        int sqltier;
        int sqlfound; //0 = false; 1 = true;
        int sqlopened; //0 = false; 1 = true;

        String sql2 = "SELECT * FROM `chests` WHERE `number` = '" + sqlnm + "'";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql2);

            // iterate through the java resultset
            while (rs.next()) {

                sqlx = rs.getInt("x");
                sqly = rs.getInt("y");
                sqlz = rs.getInt("z");
                sqltier = rs.getInt("tier");
                sqlfound = rs.getInt("found");
                sqlopened = rs.getInt("opened");

                ar[0] = sqlnm;
                ar[1] = sqlx;
                ar[2] = sqly;
                ar[3] = sqlz;
                ar[4] = sqltier;
                ar[5] = sqlfound;
                ar[6] = sqlopened;
                ar[7] = 1;

            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            ar[7] = 0;
            debugActive(false,"Could not retrieve chest# " + sqlnm + " data!!");
        }
        return ar;
    }

    public static void chestIsFound(String name, int x, int y, int z){

        int[] value = chestNumberReturn(x, y, z);
        int sqlnm = value[0];
        int[] chest = getChestFromNum(sqlnm);
        int sqlx = chest[1];
        int sqly = chest[2];
        int sqlz = chest[3];
        int sqltier = chest[4];
        int sqlfound = chest[5]; //0 = false; 1 = true;

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
                String sql3 = "UPDATE `chests` SET `found` = '1' WHERE `number` = ?";

                try {
                    PreparedStatement stmt = con.prepareStatement(sql3);
                    stmt.setInt(1, sqlnm);
                    stmt.executeUpdate();
                    debugActive(false,"Chest # " + sqlnm + ",Tier: " + sqltier + " was found by player: " + name + "");
                } catch (SQLException e) {
                    //e.printStackTrace();
                    debugActive(false,"Could not update chest found by: " + name + " !!");
                }

                //Update current player number of chest found
                String sql4 = "UPDATE `loots` SET `total_amount` = ?, `one_star` = ?, `two_star` = ?, `three_star` = ?, `four_star` = ?, `five_star` = ? WHERE `loots`.`name` = ?";

                try {
                    PreparedStatement stmt = con.prepareStatement(sql4);
                    stmt.setInt(1, newtot);
                    stmt.setInt(2, newone);
                    stmt.setInt(3, newtwo);
                    stmt.setInt(4, newthree);
                    stmt.setInt(5, newfour);
                    stmt.setInt(6, newfive);
                    stmt.setString(7, name);
                    stmt.executeUpdate();
                    debugActive(false,"Updated player: " + name + " Chest data");
                } catch (SQLException e) {
                    //e.printStackTrace();
                    debugActive(false,"Could not update player: " + name + " Chest data!!");
                }
            }
        }
    }

    static int[] addPlayerChestCount(int tier, int tot, int one, int two, int three, int four, int five){

        int caltot = tot;
        int calone = one;
        int caltwo = two;
        int calthree = three;
        int calfour = four;
        int calfive = five;

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

    public static void chestHasOpen(int num){

        //Mark the chest as open
        String sql = "UPDATE `chests` SET `opened` = '1' WHERE `number` = ?";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, num);
            stmt.executeUpdate();
            debugActive(false,"Chest # " + num + ", was opened");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(false,"Could not update chest # " + num + " !!");
        }

    }

    static void deleteChest(){

        int count=0;
        while (count <= MaxCrates) {
            DespawnChest(count, false, true);
            count++;
        }
    }

    static void removeChestsFromDb() {
        String sql = "TRUNCATE TABLE `chests`";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            debugActive(false,"Chests cleared from Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(false,"Could not remove all the chests from the Database !!");
        }
    }

    public static void removeChestEvent(int x, int y, int z) {

       int[] num = chestNumberReturn(x, y, z);

        String sql = "DELETE FROM `chests` WHERE number = ?";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, num[0]);
            stmt.executeUpdate();
            debugActive(false,"Chest cleared from Database");
        } catch (SQLException e) {
            //e.printStackTrace();
            debugActive(false,"Could not remove the chest from the Database !!");
        }
    }
}