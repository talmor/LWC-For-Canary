package com.griefcraft.sql;

import com.griefcraft.logging.Logger;
import com.griefcraft.util.ConfigValues;
import java.sql.Connection;
import java.sql.DriverManager;

public abstract class Database {
    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    public Connection connection = null;

    private static boolean connected = false;

    public static boolean isConnected() {
        return connected;
    }

    public boolean connect() throws Exception {
        if (this.connection != null) {
            return true;
        }

        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + getDatabasePath());
        connected = true;

        return true;
    }

    public String getDatabasePath() {
        return ConfigValues.DB_PATH.getString();
    }

    public abstract void load();

    public void log(String str) {
        this.logger.info(str);
    }
}
