package db;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;

import static db.DataUpdater.CAT_N;
import static db.DataUpdater.versionFileName;


class DBMover {
    static final String tableName = "Info";

    static final String[] dbNames = {"trash.db", "cafe.db"};
    static final String[] folderNames = {"trash", "cafe"};
    private static int[] versions;
    private static File versionFile;

    static void commitChanges() throws IOException {
        boolean q = false;
        versionFile = new File(versionFileName);
        if (!versionFile.exists()) {
            if (!versionFile.createNewFile()) {
                throw new IOException("Couldn't create version file");
            }
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
            updateDataBases();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateDataBases() {
        try {
            for (int i = 0; i < CAT_N; i++) {
                try (Connection c = DriverManager.getConnection("jdbc:sqlite:diff_" + dbNames[i]);
                     Statement st = c.createStatement();
                     ResultSet r = DBUtil.getSelectResult(st)) {
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

    private static void updateFile(int num) throws IOException {
        Files.move(new File("diff_" + dbNames[num]).toPath(), (new File(folderNames[num] + "/" +
                ++versions[num] + ".db")).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void updateVersionFile() throws IOException {
        Files.delete(versionFile.toPath());
        Files.createFile(versionFile.toPath());
        BufferedWriter out = new BufferedWriter(new FileWriter(versionFile));
        for (int i = 0; i < CAT_N; i++) {
            out.write(versions[i] + "\n");
        }
        out.close();
    }


}
