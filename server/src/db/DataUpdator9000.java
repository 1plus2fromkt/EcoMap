package db;

import places.Recycle.RecycleHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import static db.DBMover.*;

public class DataUpdator9000 {

    static final int CAT_N = 2;
    public static final String versionFileName = "db_versions.txt";
    public static final int TRASH_N = 0;
    static final int CAFE_N = 1;

    private static Connection[] curr = new Connection[CAT_N], diff = new Connection[CAT_N], temp = new Connection[CAT_N];
    private static final String[][] tabNames = {{"id", "lat", "lng", "rate", "title", "content_text", "address",
                                            "img_link", "info", "work_time", "site", "telephone", "e_mail"}, {"id"}};
    private static final String[][] tabTypes =
            {{"INT PRIMARY KEY", "DOUBLE", "DOUBLE", "DOUBLE", "TEXT", "TEXT", "TEXT", "TEXT"
                    , "TEXT", "TEXT", "TEXT", "TEXT", "TEXT"}, {"INT PRIMARY KEY"}};
    private static String[] schemas = new String[CAT_N];
    private static final int[] TAB_N = {tabNames[TRASH_N].length, tabNames[CAFE_N].length};
    public static void main() {
        initSchema();
        try {
            Class.forName("org.sqlite.JDBC");
            for (int i = 0; i < CAT_N; i++) {
                temp[i] = null;
                curr[i] = null;
                diff[i] = null;
                new File("temp_" + dbNames[i]).delete();
                new File("diff_" + dbNames[i]).delete();
                if (!Files.exists(Paths.get(folderNames[i])))
                    new File(folderNames[i]).mkdir();
            }
            for (int i = 0; i < CAT_N; i++) {
                temp[i] = DriverManager.getConnection("jdbc:sqlite:temp_" + dbNames[i]);
                curr[i] = DriverManager.getConnection("jdbc:sqlite:" + folderNames[i] + "/" + dbNames[i]);
                try (Statement tr = curr[i].createStatement()) {
                    if (!curr[i].getMetaData().getTables(null, null, tableName, null).next())
                        tr.execute(schemas[i]);
                }
                diff[i] = DriverManager.getConnection("jdbc:sqlite:diff_" + dbNames[i]);
                try (Statement s1 = temp[i].createStatement(); Statement s2 = diff[i].createStatement()){
                    s1.execute(schemas[i]);
                    s2.execute(schemas[i]);
                }
            }

            RecycleHandler.updateData(temp[TRASH_N]);
            updateDB(curr[TRASH_N], temp[TRASH_N], diff[TRASH_N], TRASH_N);
            DBMover.main(null);

        } catch ( Exception e ) {
            System.err.println("Oh, shit, couldn't create a database");
            e.printStackTrace();
        } finally {
            try {
                for (int i = 0; i < CAT_N; i++) {
                    if (temp[i] != null)
                        temp[i].close();
                    if (diff[i] != null)
                        diff[i].close();
                    if (curr[i] != null)
                        curr[i].close();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private static void updateDB(Connection old, Connection notOld, Connection diff, int cat) throws SQLException {
        getDifferenceOldNew(old, notOld, diff, cat);
        new File("temp_" + dbNames[cat]).renameTo(new File(folderNames[cat] + "/" + dbNames[cat]));

    }

    private static void getDifferenceOldNew(Connection old, Connection notOld, Connection diff, int cat) throws SQLException {
        try (Statement oldSt = old.createStatement();
            Statement notOldSt = notOld.createStatement();
            ResultSet second = notOldSt.executeQuery("SELECT * FROM " + tableName);
            Statement diffSt = diff.createStatement()) {
            boolean toUpdate, empty;
            String val;
            while (second.next()) {
                val = "\'";
                try (
                    ResultSet res = oldSt.executeQuery("SELECT * FROM " + tableName + " WHERE id=" + second.getObject(1).toString() + ";");
                ) {
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
            }

            addDeleted(old, notOld, diff, cat);
        }
    }

    private static void addDeleted (Connection old, Connection notOld, Connection diff, int cat) throws SQLException {
        String e = "";
        for (int i = 2; i < TAB_N[cat]; i++) {
            e += ",\'\'";
        }
        try (
        Statement oldSt = old.createStatement();
        Statement notOldSt = notOld.createStatement();
        Statement diffSt = diff.createStatement();
        ResultSet res = oldSt.executeQuery("SELECT * FROM " + tableName + ";")) {
            while (res.next()) {
                try (
                        ResultSet result = notOldSt.executeQuery("SELECT * FROM " + tableName + " WHERE id=" + res.getObject(1).toString() + ";")
                ) {
                    if (!result.next()) {
                        System.out.println(res.getObject(1).toString());
                        diffSt.execute(getInsertScheme(cat, res.getObject(1).toString() + e + ",\'DELETE ME PLS\');", false));
                    }
                }
            }
        }
    }

    public static String getInsertScheme(int number, String s, boolean replace) {
        String sch = (replace ? "REPLACE" : "INSERT") + " INTO " + DBMover.tableName + " (";
        for (int i = 0; i < tabNames[number].length; i++)
            sch += tabNames[number][i] + ((i == tabNames[number].length - 1) ? ")" : ", ");
        sch += " VALUES (" + s + ");";
        return sch;
    }


    private static void initSchema() {
        if (schemas[0] != null)
            return;
        for (int tr = 0; tr < CAT_N; tr++) {
            schemas[tr] = "CREATE TABLE " + tableName +
                    "(";
            for (int i = 0; i < TAB_N[tr]; i++) {
                schemas[tr] += tabNames[tr][i] + " " + tabTypes[tr][i] + ((i == TAB_N[tr] - 1) ? "" : ", ");
            }
            schemas[tr] += ");";
        }
    }

    public static void mergeChanges(Connection c, int category, int lastVersion, int currVersion) {
        try {
            initSchema();
            c.createStatement().execute(schemas[category]);
            for (int i = lastVersion + 1; i <= currVersion; i++) {

                try (Statement oldSt = c.createStatement();
                     Connection d = DriverManager.getConnection("jdbc:sqlite:" + folderNames[category] + "/" +
                        i + ".db");
                     Statement newSt = d.createStatement();
                     ResultSet newR = newSt.executeQuery("SELECT * FROM " + tableName)) {
                     while (newR.next()) {
                         String val = "\'";
                         for (int j = 1; j <= TAB_N[category]; j++) {
                             val += newR.getObject(j).toString() + ((j == TAB_N[category]) ? "\');" : "\', \'");
                         }
                         oldSt.execute(getInsertScheme(category, val, true));
                     }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String dbFileName(int cat) {
        return folderNames[cat] + "/" + dbNames[cat];
    }
}
