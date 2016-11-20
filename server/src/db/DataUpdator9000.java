package db;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import static db.DBMover.*;

public class DataUpdator9000 {

    private static Connection[] curr = new Connection[CAT_N], diff = new Connection[CAT_N], temp = new Connection[CAT_N];
    static final String[][] tabNames = {{"id", "lat", "lng", "rate", "title", "content_text", "address",
                                            "img_link", "info", "work_time", "site", "telephone", "e_mail"}, {"id"}};
    private static final String[][] tabTypes =
            {{"INT PRIMARY KEY", "DOUBLE", "DOUBLE", "DOUBLE", "TEXT", "TEXT", "TEXT", "TEXT"
                    , "TEXT", "TEXT", "TEXT", "TEXT", "TEXT"}, {"INT PRIMARY KEY"}};
    private static String[] schemas = new String[CAT_N];
    static final int[] TAB_N = {tabNames[TRASH_N].length, tabNames[CAFE_N].length};
    public static void main(String[] args) {
        initSchema();
        try {
            Class.forName("org.sqlite.JDBC");
            for (int i = 0; i < CAT_N; i++) {
                new File("temp_" + dbNames[i]).delete();
                new File("diff_" + dbNames[i]).delete();
                if (!Files.exists(Paths.get(folderNames[i])))
                    new File(folderNames[i]).mkdir();
            }
            for (int i = 0; i < CAT_N; i++) {
                temp[i] = DriverManager.getConnection("jdbc:sqlite:temp_" + dbNames[i]);
                curr[i] = DriverManager.getConnection("jdbc:sqlite:" + folderNames[i] + "/" + dbNames[i]);
                Statement tr = curr[i].createStatement();
                if (!curr[i].getMetaData().getTables(null, null, tableName, null).next())
                    tr.execute(schemas[i]);
                diff[i] = DriverManager.getConnection("jdbc:sqlite:diff_" + dbNames[i]);
                temp[i].createStatement().execute(schemas[i]);
                diff[i].createStatement().execute(schemas[i]);
            }

            RecycleHandler.updateData(temp[TRASH_N]);
            updateDB(curr[TRASH_N], temp[TRASH_N], diff[TRASH_N], TRASH_N);

        } catch ( Exception e ) {
            System.err.println("Oh, shit, couldn't create a database");
            e.printStackTrace();
        }
    }


    private static void updateDB(Connection old, Connection notOld, Connection diff, int cat) throws SQLException {
        DBEquality(old, notOld, diff, cat);
        old.close();
        notOld.close();
        new File("temp_" + dbNames[cat]).renameTo(new File(folderNames[cat] + "/" + dbNames[cat]));

    }

    private static void DBEquality(Connection old, Connection notOld, Connection diff, int cat) throws SQLException {
        ResultSet res;
        Statement oldSt = old.createStatement();
        Statement notOldSt = notOld.createStatement();
        ResultSet second = notOldSt.executeQuery("SELECT * FROM " + tableName);
        Statement diffSt = diff.createStatement();
        boolean toUpdate, empty;
        String val;
        while (second.next()) {
            val = "\'";
            res = oldSt.executeQuery("SELECT * FROM " + tableName + " WHERE id=" + second.getObject(1).toString() + ";");
            toUpdate = false;
            res.next();
            empty = res.getObject(1) == null;
            for (int i = 1; i <= TAB_N[cat]; i++) {
                val += second.getObject(i).toString() + ((i == TAB_N[cat]) ? "\');" : "\', \'");
                if (!empty && !res.getObject(i).equals(second.getObject(i))) {
                    toUpdate = true;
                }
            }
            if (toUpdate) {
                diffSt.execute(getInsertScheme(cat, val, false));
            }
        }
        addDeleted(old, notOld, diff, cat);
    }

    private static void addDeleted (Connection old, Connection notOld, Connection diff, int cat) throws SQLException {
        String e = "";
        for (int i = 2; i < TAB_N[cat]; i++) {
            e += ",\'\'";
        }
        ResultSet res, result;
        Statement oldSt = old.createStatement();
        Statement notOldSt = notOld.createStatement();
        Statement diffSt = diff.createStatement();
        res = oldSt.executeQuery("SELECT * FROM " + tableName + ";");
        while (res.next()) {
            result = notOldSt.executeQuery("SELECT * FROM " + tableName + " WHERE id=" + res.getObject(1).toString() + ";");
            if (!result.next()) {
                System.out.println(res.getObject(1).toString());
                diffSt.execute(getInsertScheme(cat, res.getObject(1).toString() + e + ",\'DELETE ME PLS\');", false));
            }
        }
    }

    static String getInsertScheme(int number, String s, boolean replace) {
        String sch = (replace ? "REPLACE" : "INSERT") + " INTO " + DBMover.tableName + " (";
        for (int i = 0; i < DataUpdator9000.tabNames[number].length; i++)
            sch += DataUpdator9000.tabNames[number][i] + ((i == DataUpdator9000.tabNames[number].length - 1) ? ")" : ", ");
        sch += " VALUES (" + s + ");";
        return sch;
    }


    private static void initSchema() {
        for (int tr = 0; tr < CAT_N; tr++) {
            schemas[tr] = "CREATE TABLE " + tableName +
                    "(";
            for (int i = 0; i < TAB_N[tr]; i++) {
                schemas[tr] += tabNames[tr][i] + " " + tabTypes[tr][i] + ((i == TAB_N[tr] - 1) ? "" : ", ");
            }
            schemas[tr] += ");";
        }
    }
}
