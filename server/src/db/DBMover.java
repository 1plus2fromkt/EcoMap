package db;

import java.io.*;
import java.sql.*;

import static db.DataUpdator9000.TAB_N;

public class DBMover {
    static final int CAT_N = 2, TRASH_N = 0, CAFE_N = 1;
    static final String tableName = "Info";
    static final String versionFileName = "db_versions.txt";
    static String[] dbNames = {"trash.db", "cafe.db"};
    static String[] folderNames = {"trash", "cafe"};
    private static int[] versions;
    static File versionFile;
    public static void main(String[] args) throws IOException {
        boolean q = false;
        versionFile = new File(versionFileName);
        if (!versionFile.exists()) {
            versionFile.createNewFile();
            q = true;
        }
        update(q);
    }

    private static void update(boolean initVersions) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(versionFile));
            if (initVersions) {
                String s = "";
                for (int i = 0; i < CAT_N; i++) {
                    s += "0\n";
                }
                out.write(s);
            }
            out.close();
            BufferedReader in = new BufferedReader(new FileReader(versionFile));
            versions = new int[CAT_N];
            for (int i = 0; i < CAT_N; i++) {
                try {
                    versions[i] = Integer.parseInt(in.readLine());
                } catch (Exception e) {
                    versions[i] = 0;
                }
            }
            DataUpdator9000.main(null);
            updateFiles();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateFiles() {
        try {
            Class.forName("org.sqlite.JDBC");
            for (int i = 0; i < CAT_N; i++) {
                Connection c = DriverManager.getConnection("jdbc:sqlite:diff_" + dbNames[i]);
                Statement st = c.createStatement();
                ResultSet r = st.executeQuery("SELECT * FROM " + tableName);
                if (r.next()) {
                    updateFile(i);
                }
            }
            updateVersionFile();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateFile(int num) {
        new File("diff_" + dbNames[num]).renameTo(new File(folderNames[num] + "/" +
                Integer.toString(++versions[num]) + ".db"));
    }

    private static void updateVersionFile() throws IOException {
        versionFile.delete();
        versionFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(versionFile));
        for (int i = 0; i < CAT_N; i++) {
            out.write(Integer.toString(versions[i]) + "\n");
        }
        out.close();
    }

    public static void mergeChanges(Connection c, int category, int lastVersion, int currVersion) {
        try {
            for (int i = lastVersion; i < currVersion; i++) {
                Statement oldSt = c.createStatement();
                Connection d = DriverManager.getConnection("jdbc:sqlite:" + folderNames[category] + "/" +
                                            Integer.toString(i) + ".db");
                Statement newSt = d.createStatement();
                ResultSet newR = newSt.executeQuery("SELECT * FROM " + tableName);
                while (newR.next()) {
                    String val = "\'";
                    for (int j = 1; j <= TAB_N[category]; j++) {
                        val += newR.getObject(j).toString() + ((j == TAB_N[category]) ? "\');" : "\', \'");
                    }
                    oldSt.execute(DataUpdator9000.getInsertScheme(category, val, true));
                }
                d.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
