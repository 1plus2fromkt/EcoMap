package db;

import places.Recycle.RecycleHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;

import static db.DBUtil.closeIfNeeded;
import static db.DBUtil.getSelectResult;
import static db.DBMover.*;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.deleteIfExists;

public class DataUpdater {

    public static final String versionFileName = "db_versions.txt";
    public static final int TRASH_N = 0, CAFE_N = 1, CAT_N = 1;
    static final String[][] tabNames = {{"id", "lat", "lng", "rate", "title", "content_text", "address",
            "img_link", "info", "work_time", "site", "telephone", "e_mail"}, {"id"}},
            tabTypes = {{"INT PRIMARY KEY", "DOUBLE", "DOUBLE", "DOUBLE", "TEXT", "TEXT", "TEXT", "TEXT"
                    , "TEXT", "TEXT", "TEXT", "TEXT", "TEXT"}, {"INT PRIMARY KEY"}};
    private static final int[] TAB_N = {tabNames[TRASH_N].length, tabNames[CAFE_N].length};
    private static Connection[] curr = new Connection[CAT_N], diff = new Connection[CAT_N], temp = new Connection[CAT_N];
    private static String[] schemas = new String[CAT_N];

    public static void updateData() {
        System.out.println("Starting to update data");
        initSchemas();
        try {
            Class.forName("org.sqlite.JDBC");
            initConnections();
            System.out.println("Update trashes");
            RecycleHandler.updateData(temp[TRASH_N]);
            System.out.println("Committing changes");
            for (int i = 0; i < CAT_N; i++) {
                updateDB(curr[i], temp[i], diff[i], i);
            }
            DBMover.commitChanges();
            System.out.println("Chages commited successfully");
        } catch (Exception e) {
            System.err.println("Oh, shit, couldn't create a database");
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    static void initConnections() throws SQLException, IOException {
        for (int i = 0; i < CAT_N; i++) {
            temp[i] = null;
            curr[i] = null;
            diff[i] = null;
            deleteIfExists(new File("temp_" + dbNames[i]).toPath());
            deleteIfExists(new File("diff_" + dbNames[i]).toPath());
            if (!Files.exists(Paths.get(folderNames[i])))
                Files.createDirectory(new File(folderNames[i]).toPath());
            temp[i] = DriverManager.getConnection("jdbc:sqlite:temp_" + dbNames[i]);
            curr[i] = DriverManager.getConnection("jdbc:sqlite:" + folderNames[i] + "/" + dbNames[i]);
            try (Statement tr = curr[i].createStatement()) {
                if (!curr[i].getMetaData().getTables(null, null, tableName, null).next())
                    tr.execute(schemas[i]);
            }
            diff[i] = DriverManager.getConnection("jdbc:sqlite:diff_" + dbNames[i]);
            try (Statement s1 = temp[i].createStatement();
                 Statement s2 = diff[i].createStatement()) {
                s1.execute(schemas[i]);
                s2.execute(schemas[i]);
            }
        }
    }

    private static void closeConnections() {
        try {
            for (int i = 0; i < CAT_N; i++) {
                closeIfNeeded(temp[i]);
                closeIfNeeded(diff[i]);
                closeIfNeeded(curr[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void updateDB(Connection old, Connection newCon, Connection diff, int cat) throws SQLException, IOException {
        getDifference(old, newCon, diff, cat);
        Files.move(new File("temp_" + dbNames[cat]).toPath(), (new File(folderNames[cat] + "/" + dbNames[cat])).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

    private static void getDifference(Connection old, Connection newCon, Connection diff, int category) throws SQLException {
        diff.setAutoCommit(false);
        try (Statement oldSt = old.createStatement();
             Statement newConStatement = newCon.createStatement();
             ResultSet second = getSelectResult(newConStatement);
             Statement diffSt = diff.createStatement()) {
            boolean toUpdate, empty;
            String val;
            while (second.next()) {
                val = "\'";
                try (
                        ResultSet res = getSelectResult(oldSt, tableName, "WHERE id=" + second.getInt(1));
                ) {
                    toUpdate = false;
                    res.next();
                    empty = res.getObject(1) == null;
                    for (int i = 1; i <= TAB_N[category]; i++) {
                        val += second.getObject(i).toString() + ((i == TAB_N[category]) ? "\');" : "\', \'");
                        if (!empty && !res.getObject(i).equals(second.getObject(i))) {
                            toUpdate = true;
                        }
                    }
                    if (toUpdate) {
                        diffSt.execute(DBUtil.getInsertSchema(category, val, false));
                    }
                }
            }
            addRowsToDelete(old, newCon, diff, category);
            diff.commit();
        }
        diff.setAutoCommit(true);
    }


    private static void addRowsToDelete(Connection old, Connection newCon, Connection diff, int cat) throws SQLException {
        String e = "";
        for (int i = 2; i < TAB_N[cat]; i++) {
            e += ",\'\'";
        }
        try (
                Statement oldSt = old.createStatement();
                Statement newConStatement = newCon.createStatement();
                Statement diffSt = diff.createStatement();
                ResultSet res = getSelectResult(oldSt)) {
            while (res.next()) {
                try (
                        ResultSet result = getSelectResult(newConStatement, tableName)
                ) {
                    if (!result.next()) {
                        System.out.println(res.getObject(1).toString());
                        diffSt.execute(DBUtil.getInsertSchema(cat, res.getObject(1).toString() + e + ",\'DELETE ME PLS\');", false));
                    }
                }
            }
        }
    }


    private static void initSchemas() {
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

    public static void mergeChanges(Connection c, int category, int lastVersion, int currVersion) throws SQLException {
        initSchemas();
        c.createStatement().execute(schemas[category]);
        for (int i = lastVersion + 1; i <= currVersion; i++) {
            try (Statement oldSt = c.createStatement();
                 Connection d = DriverManager.getConnection("jdbc:sqlite:" + folderNames[category] + "/" +
                         i + ".db");
                 Statement newSt = d.createStatement();
                 ResultSet newR = getSelectResult(newSt)) {
                while (newR.next()) {
                    String val = "\'";
                    for (int j = 1; j <= TAB_N[category]; j++) {
                        val += newR.getObject(j).toString() + ((j == TAB_N[category]) ? "\');" : "\', \'");
                    }
                    oldSt.execute(DBUtil.getInsertSchema(category, val, true));
                }
            }
        }

    }

    public static String dbFileName(int cat) {
        return folderNames[cat] + "/" + dbNames[cat];
    }
}
