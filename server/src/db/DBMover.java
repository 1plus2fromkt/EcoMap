package db;

import java.io.*;
import java.sql.*;

import static db.DataUpdator9000.CAT_N;
import static db.DataUpdator9000.versionFileName;


class DBMover {
    static final String tableName = "Info";

    static String[] dbNames = {"trash.db", "cafe.db"};
    static String[] folderNames = {"trash", "cafe"};
    private static int[] versions;
    private static File versionFile;
    static void main(String[] args) throws IOException {
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
            if (initVersions) {
                BufferedWriter out = new BufferedWriter(new FileWriter(versionFile));
                String s = "";
                for (int i = 0; i < CAT_N; i++) {
                    s += "0\n";
                }
                out.write(s);
                out.close();
            }
            BufferedReader in = new BufferedReader(new FileReader(versionFile));
            versions = new int[CAT_N];
            for (int i = 0; i < CAT_N; i++) {
                try {
                    versions[i] = Integer.parseInt(in.readLine());
                } catch (Exception e) {
                    versions[i] = 0;
                }
            }
            updateFiles();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateFiles() {
        try {
            for (int i = 0; i < CAT_N; i++) {
                try (Connection c = DriverManager.getConnection("jdbc:sqlite:diff_" + dbNames[i]);
                     Statement st = c.createStatement();
                     ResultSet r = st.executeQuery("SELECT * FROM " + tableName)) {
                    if (r.next()) {
                        updateFile(i);
                    }
                }
            }
            updateVersionFile();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateFile(int num) {
        new File("diff_" + dbNames[num]).renameTo(new File(folderNames[num] + "/" +
                ++versions[num] + ".db"));
    }

    private static void updateVersionFile() throws IOException {
        versionFile.delete();
        versionFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(versionFile));
        for (int i = 0; i < CAT_N; i++) {
            out.write(versions[i] + "\n");
        }
        out.close();
    }


}
