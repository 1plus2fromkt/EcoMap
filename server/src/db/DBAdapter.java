package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBAdapter {
    public static String getInsertSchema(int number, String s, boolean replace) {
        String sch = (replace ? "REPLACE" : "INSERT") + " INTO " + DBMover.tableName + " (";
        for (int i = 0; i < DataUpdater.tabNames[number].length; i++)
            sch += DataUpdater.tabNames[number][i] + ((i == DataUpdater.tabNames[number].length - 1) ? ")" : ", ");
        sch += " VALUES (" + s + ");";
        return sch;
    }

    public static ResultSet getSelectResult(Statement st, String tableName) throws SQLException {
        return getSelectResult(st, tableName, "");
    }

    public static ResultSet getSelectResult(Statement st, String tableName, String filter) throws SQLException {
        return st.executeQuery("SELECT * FROM " + tableName + " " + filter + ";");
    }
}
