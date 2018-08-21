import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Main {
    private final static String CFG_MYSQL_SERVER = "jdbc:mysql://localhost:3306/mc_coreprotect?verifyServerCertificate=false&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final static String CFG_DB_USERNAME = "MC_CoreProtect";
    private final static String CFG_DB_PASSWORD = "<<PASSWORD>>";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        HashMap<Coordinate, Integer> hmap = new HashMap<Coordinate, Integer>();
        Integer blockId = 0;
        Boolean forward = true;

        try {
            conn = DriverManager.getConnection(CFG_MYSQL_SERVER, CFG_DB_USERNAME, CFG_DB_PASSWORD);
            stmt = conn.createStatement();
            String sql;
            ResultSet rs;
            System.out.println("Got connection");

            while (forward) {
                Integer startBlockId = blockId;
                System.out.println("Executing SQL. BlockId: " + blockId + " Hash Size: " + hmap.size());

                sql = "SELECT `rowid`, `time`, `x`, `y`, `z` FROM `mc_block` WHERE `rowid` > " + blockId + " and `action` < 2 limit 1000000";
                rs = stmt.executeQuery(sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();

                while (rs.next()) {
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    boolean alreadyMined = hmap.containsKey(new Coordinate(x, y, z));
                    if (!alreadyMined) {
                        hmap.put(new Coordinate(x, y, z), rs.getInt("rowid"));
                    }
                    blockId = rs.getInt("rowid");
                }
                if (startBlockId == blockId) forward = false;
            }
            System.out.println("Finished! HashMap Size: " + hmap.size());
            int count = 0;
            int batchSize = 50000;
            LinkedList<Integer> hmapPart = new LinkedList<>();

            for (Integer value : hmap.values()) {
                    hmapPart.add(value);
                if (++count % batchSize == 0) {
                    Runnable r = new updateThread(new LinkedList<>(hmapPart));
                    new Thread(r).start();
                    System.out.println("Started new Thread");
                    hmapPart = new LinkedList<>();
                }
            }

            // Drop the table from previous use
            sql = "DROP TABLE mc_stats_pretty";
            stmt.execute(sql);

            // Create the table again
            sql = "CREATE TABLE `mc_coreprotect`.`mc_stats_pretty` ( `user` INT NOT NULL ) ENGINE = InnoDB;";
            stmt.execute(sql);

            // Insert users in table mc_stats_pretty
            sql = "INSERT INTO mc_stats_pretty (user) SELECT user from mc_stats GROUP BY user";
            stmt.execute(sql);

            // Update statistics table
            sql = "SELECT block, data FROM `mc_stats` WHERE block in (select type from mc_blocknames) AND data in (SELECT type2 from mc_blocknames) GROUP BY block, data";
            rs = stmt.executeQuery(sql);

            ArrayList<int[]> combinations = new ArrayList<int[]>();
            while (rs.next()) {
                int[] arr = new int[2];
                arr[0] = rs.getInt("block");
                arr[1] = rs.getInt("data");
                combinations.add(arr);
            }

            for (int[] value : combinations) {
                // Get Column name
                sql = "SELECT name FROM `mc_blocknames` WHERE type = " + value[0] + " and type2 = " + value[1] + " limit 1";
                rs = stmt.executeQuery(sql);
                String columnName = "";

                while (rs.next()) {
                    columnName = rs.getString("name");
                    System.out.println(columnName);
                }

                if (columnName.length() > 0 && !(columnName.charAt(columnName.length() - 1) == ' ')) {
                    // Insert stats into mc_stats_pretty

                    try {
                        sql = "ALTER TABLE mc_stats_pretty ADD COLUMN `" + columnName + "` INT NOT NULL";
                        stmt.execute(sql);

                        // Add counts to mc_stats_pretty
                        sql = "update mc_stats_pretty p left join mc_stats s on p.user = s.user set `" + columnName + "` = s.total WHERE s.block = " + value[0] + " AND s.data = " + value[1];
                        stmt.execute(sql);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Got exception");
            e.printStackTrace();
        }
        finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}

class updateThread implements Runnable {

    private final static String CFG_MYSQL_SERVER = "jdbc:mysql://localhost:3306/mc_coreprotect?verifyServerCertificate=false&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final static String CFG_DB_USERNAME = "MC_CoreProtect";
    private final static String CFG_DB_PASSWORD = "<<PASSWORD>>";

    private LinkedList<Integer> hmap;

    public updateThread(LinkedList<Integer> hmap) {
        // store parameter for later user
        this.hmap = hmap;
    }

    public void run() {
        System.out.println("Hi, i'm  new thread. Starting value is: ");
        try {
            final int batchSize = 10000;
            int count = 0;
            int countHmap = 0;
            String sql = "UPDATE `mc_block` SET `mined_first` = 1 WHERE rowid = ?";
            Connection conn = DriverManager.getConnection(CFG_MYSQL_SERVER, CFG_DB_USERNAME, CFG_DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql);

            for (Integer value : hmap) {
                ps.setInt(1, value);
                ps.addBatch();

                if (++count % batchSize == 0) {
                    countHmap = countHmap + batchSize;
                    System.out.println("Executed Batch. Count: " + countHmap);
                    ps.executeBatch();
                    ps.clearBatch(); //not sure if this is necessary
                }
            }
            ps.executeBatch();   // flush the last few records.
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Thread done.");
    }
}
