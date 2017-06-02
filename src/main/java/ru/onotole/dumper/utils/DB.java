package ru.onotole.dumper.utils;

import ru.onotole.dumper.model.Tank;
import ru.onotole.dumper.model.TankPerUserStat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by onotole on 27/05/2017.
 */
public class DB {
    private Connection connection;
    private Statement statement;
    private final static String TANK_USER_STAT_TABLE = "tank_user_stat";
    private final static String TANK_DATA_TABLE = "tank_data";
    private final static String DB_NAME = "tanks.db";

    public DB() {
        createUserTankDB();
        createTankDataDb();
    }

    public void connect(String dbName) {
        if (connection != null) return;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String databaseUrl = "jdbc:sqlite:" + dbName;
        try {
            connection = DriverManager.getConnection(databaseUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearDB(String dbName) {
        doUpdateRequest(String.format("DELETE FROM %s;", dbName), dbName);
    }

    public void dropDB() {
        dropDB(TANK_USER_STAT_TABLE);
    }

    public void createUserTankDB() {
        createUserTankDB(TANK_USER_STAT_TABLE);
    }

    public void putDataToDB(TankPerUserStat data) {
        putDataToDB(TANK_USER_STAT_TABLE, data);
    }

    public void putTankDataToDB(Tank tank) {
        String req = String.format("INSERT INTO %s " +
                        "(NAME, TANK_ID, TIER, NATION, TYPE, MANEUVERABILITY, PROTECTION, SHOT_EFFICIENCY, FIREPOWER) VALUES (" +
                        "\"%s\", \"%d\", \"%d\", \"%s\", \"%s\", \"%d\", \"%d\", \"%d\", \"%d\"); ",
                TANK_DATA_TABLE,
                tank.getName(),
                tank.getTank_id(),
                tank.getTier(),
                tank.getNation(),
                tank.getType(),
                tank.getDefault_profile().getManeuverability(),
                tank.getDefault_profile().getProtection(),
                tank.getDefault_profile().getShot_efficiency(),
                tank.getDefault_profile().getFirepower()
        );
        doUpdateRequest(req, TANK_DATA_TABLE);
    }

    private void dropDB(String dbName) {
        doUpdateRequest(String.format("DELETE FROM %s;", dbName), dbName);
    }

    private void createUserTankDB(String dbName) {
        String req = String.format("CREATE TABLE IF NOT EXISTS %s (ACCOUNT_ID INTEGER, TANK_ID INTEGER, BATTLES INTEGER," +
                "WINS INTEGER);", dbName);
        doUpdateRequest(req, dbName);
    }

    private void createTankDataDb() {
        String req = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                "NAME TEXT," +
                "TANK_ID INTEGER," +
                "TIER INTEGER," +
                "NATION TEXT," +
                "TYPE TEXT," +
                "MANEUVERABILITY INTEGER," +
                "PROTECTION INTEGER," +
                "SHOT_EFFICIENCY INTEGER," +
                "FIREPOWER INTEGER);",
                TANK_DATA_TABLE
        );
        doUpdateRequest(req, TANK_DATA_TABLE);
    }

    private void doUpdateRequest(String request, String dbName) {
        dbName = "tank_sample_big.db";
        if (connection == null) connect(dbName);
        int  result = 0;
        try(Statement updateStmt = connection.createStatement()) {
            result = updateStmt.executeUpdate(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void putDataToDB(String dbName, TankPerUserStat data) {
        String req = String.format("INSERT INTO %s (ACCOUNT_ID, TANK_ID, BATTLES, WINS) VALUES (\"%d\", \"%d\", \"%d\"," +
                "\"%d\");", dbName, data.getUser_id(), data.getTank_id(), data.getAll().getBattles(),
                data.getAll().getWins());
        doUpdateRequest(req, dbName);
    }

}
