package com.ecomap.server.db;

import com.ecomap.server.DataModel;
import com.ecomap.server.util.Logger;

import java.sql.*;
import java.util.Objects;
import java.util.function.Function;

import static com.ecomap.server.db.DataUpdater.TABLE_NAME;

public class DBUtil {
    public static String getInsertSchema(DataModel model, String values, boolean replace) {
        StringBuilder schema = new StringBuilder();
        schema.append(replace ? "REPLACE" : "INSERT");
        schema.append(" INTO ").append(TABLE_NAME).append('(');
        for (DataModel.Entry e : model.fields) {
            schema.append(e.name).append(", ");
        }
        schema.setLength(schema.length() - 2);
        schema.append(") VALUES (").append(values).append(");");
        return schema.toString();
    }

    static ResultSet getSelectResult(Statement st) throws SQLException {
        return getSelectResult(st, TABLE_NAME);
    }

    static ResultSet getSelectResult(Statement st, String tableName) throws SQLException {
        return getSelectResult(st, tableName, "");
    }

    static ResultSet getSelectResult(Statement st, String tableName, String filter) throws SQLException {
        return st.executeQuery("SELECT * FROM " + tableName + " " + filter + ";");
    }

    static void closeIfNeeded(Connection c) throws SQLException {
        if (c != null) {
            c.close();
        }
    }

    /**
     * Create sql query to create a table in the database
     *
     * @param model Model for which to create a table
     * @return String representation of needed sql query
     */
    static String getCreateTableSchema(DataModel model) {
        StringBuilder schema = new StringBuilder();
        schema.append("CREATE TABLE ").append(TABLE_NAME).append('(');
        for (DataModel.Entry e : model.fields) {
            schema.append(e.name).append(" ").append(e.type).append(", ");
        }
        schema.setLength(schema.length() - 2);
        schema.append(");");
        return schema.toString();
    }

    /**
     * Writes difference between old connection and new connection
     * into diff connection.
     *
     * @param oldConn Old connection
     * @param newConn New connection
     * @param diffConn Connection for difference
     * @param model Data model of databases
     * @return DiffResult with result of operation
     * @throws SQLException
     */
    @SuppressWarnings("Duplicates")
    private static DiffResult getDifference(Connection oldConn, Connection newConn,
                                            Connection diffConn, DataModel model) throws SQLException {
        diffConn.setAutoCommit(false);
        DiffResult result = new DiffResult();
        try (
                Statement oldSt = oldConn.createStatement();
                Statement newSt = newConn.createStatement();
                ResultSet newRes = getSelectResult(newSt);
                Statement diffSt = diffConn.createStatement()
        ) {
            String val;
            while (newRes.next()) {
                val = "\'";
                // TODO fuck fuck fuck remove it
                Function<ResultSet, String> getFilter = rs -> {
                    if (model.id == 0) { // recycle
                        try {
                            return "WHERE id=" + rs.getInt(1);
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    } else { // ecomobile
                        try {
                            return "WHERE address=\'" + rs.getString(1) + "\'";
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                    return "";
                };
                try (
                        ResultSet oldRes = getSelectResult(oldSt, TABLE_NAME,
                                getFilter.apply(newRes))
                ) {
                    boolean toUpdate = false;
                    oldRes.next();
                    boolean empty = oldRes.getObject(1) == null;
                    for (int i = 1; i <= model.fields.size(); i++) {
                        val += newRes.getObject(i).toString() + "\', \'";
                        if (!Objects.equals(oldRes.getObject(i), newRes.getObject(i))) {
                            toUpdate = true;
                        }
                    }
                    val = val.substring(0, val.length() - 3);
                    val += ");";
                    if (empty || toUpdate) {
                        diffSt.execute(DBUtil.getInsertSchema(model, val, false));
                        if (empty) {
                            result.newItems++;
                        } else {
                            result.updatedItems++;
                        }
                    }
                }
            }
            addRowsToDelete(oldConn, newConn, diffConn, model, result);
            diffConn.commit();
        }
        diffConn.setAutoCommit(true);
        return result;
    }

    @SuppressWarnings("Duplicates")
    private static void addRowsToDelete(Connection oldConn, Connection newConn, Connection diffConn,
                                        DataModel model, DiffResult diffResult) throws SQLException {
        String e = "";
        for (int i = 2; i < model.fields.size(); i++) {
            e += ",\'\'";
        }
        try (
                Statement oldSt = oldConn.createStatement();
                Statement newSt = newConn.createStatement();
                Statement diffSt = diffConn.createStatement();
                ResultSet oldRes = getSelectResult(oldSt)
        ) {
            while (oldRes.next()) {
                // TODO fuck fuck fuck remove it
                Function<ResultSet, String> getFilter = rs -> {
                    if (model.id == 0) { // recycle
                        try {
                            return "WHERE id=" + rs.getInt(1);
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    } else { // ecomobile
                        try {
                            return "WHERE address=\'" + rs.getString(1) + "\'";
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                    return "";
                };
                try (
                        ResultSet result = getSelectResult(newSt, TABLE_NAME,
                                getFilter.apply(oldRes))
                ) {
                    if (!result.next()) {
                        String sch = DBUtil.getInsertSchema(model,
                                oldRes.getObject(1).toString() + e + ",\'DELETE\');", false);
                        try {
                            diffSt.execute(DBUtil.getInsertSchema(model,
                                "\'" + oldRes.getObject(1).toString() + "\'" + e + ",\'DELETE\');", false));
                        } catch (SQLException ex) {
                            Logger.log("bad schema = " + sch);
                            throw ex;
                        }
                        diffResult.deletedItems++;
                    }
                }
            }
        }
    }

    /**
     * Write difference between current database and database of specific
     * version to another database.
     *
     * @param destName Name of database where to write difference
     * @param model Model of data in databases
     * @param lastVersion Which version to compare
     */
    public static void mergeChanges(String destName, DataModel model, int lastVersion) {
        try (
                Connection destConn = DriverManager.getConnection("jdbc:sqlite:" + destName);
                Statement destSt = destConn.createStatement();
                Connection prevConn = DriverManager.getConnection(
                        "jdbc:sqlite:" + model.folderName + "/" + lastVersion + ".db");
                Connection newConn = DriverManager.getConnection(
                        "jdbc:sqlite:" + model.folderName + "/" + model.dbName)
        ) {
            String createTableSchema = DBUtil.getCreateTableSchema(model);
            destSt.execute(createTableSchema);
            getDifference(prevConn, newConn, destConn, model);
        } catch (SQLException e) {
            Logger.err("Can not merge changes");
            e.printStackTrace();
        }
    }

    public static class DiffResult {
        int newItems, updatedItems, deletedItems;

        DiffResult() {

        }

        public boolean equals(Object o) {
            return o instanceof DiffResult && equals((DiffResult) o);
        }

        private boolean equals(DiffResult r) {
            return r != null && newItems == r.newItems && updatedItems == r.updatedItems
                    && deletedItems == r.deletedItems;
        }

        public int hashCode() {
            return newItems * 67236 + updatedItems * 4325 + deletedItems * 6943;
        }

        public String toString() {
            return "new: " + newItems + "; updated: " + updatedItems + "; deleted: " + deletedItems;
        }
    }
}
