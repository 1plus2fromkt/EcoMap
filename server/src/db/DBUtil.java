package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static db.DBMover.tableName;

public class DBUtil {
    public static String getInsertSchema(int number, String s, boolean replace) {
        String sch = (replace ? "REPLACE" : "INSERT") + " INTO " + tableName + " (";
        for (int i = 0; i < DataUpdater.tabNames[number].length; i++)
            sch += DataUpdater.tabNames[number][i] + ((i == DataUpdater.tabNames[number].length - 1) ? ")" : ", ");
        sch += " VALUES (" + s + ");";
        return sch;
    }

    static ResultSet getSelectResult(Statement st) throws SQLException {
        return getSelectResult(st, tableName);
    }

    static ResultSet getSelectResult(Statement st, String tableName) throws SQLException {
//        return st.executeQuery("SELECT * FROM " + tableName + ";");
        return getSelectResult(st, tableName, "");
    }

    static ResultSet getSelectResult(Statement st, String tableName, String filter) throws SQLException {
        return st.executeQuery("SELECT * FROM " + tableName + " " + filter + ";");
    }

    static void closeIfNeeded(Connection c) throws SQLException {
        if (c != null)
            c.close();
    }
}
