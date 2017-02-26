package com.ecomap.server.db;

import com.ecomap.server.DataModel;
import com.ecomap.server.Source;
import com.ecomap.server.parser.Parser;
import com.ecomap.server.parser.Place;
import com.ecomap.server.util.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static com.ecomap.server.db.DBUtil.closeIfNeeded;

public class DataUpdater {

    private static final String VERSION_FILE_NAME = "db_versions.txt";
    private static File versionFile;

    static final String TABLE_NAME = "Info";

    private static int[] versions;

    public static final Source[] SOURCES = {Source.RECYCLE, Source.ECOMOBILE};

    static final int CAT_NUMBER = SOURCES.length;

    private static Connection currConn, tempConn, diffConn;

    public static void update() {
        Logger.log("--- starting to update data ---");
        initSqlite();

        for (Source source : SOURCES) {
            DataModel model = DataModel.read(source);
            prepareFiles(model);
            initConnections(model);
            update(source, tempConn);
            closeConnections();
            Logger.log(source + " updated");
        }
        commitChanges();

        Logger.log("--- data updated ---");
    }

    private static void update(Source source, Connection destConn) {
        List<Place> places = Parser.loadPlaces(source);
        places.forEach(p -> p.writeToDB(destConn));
    }

    private static void initSqlite() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Logger.err("Error with sqlite lib");
            e.printStackTrace();
        }
    }

    /**
     * Clear temp and diff files, create dir for files
     *
     * @param model Model of data in the database
     */
    private static void prepareFiles(DataModel model) {
        try {
            Files.deleteIfExists(new File("temp_" + model.dbName).toPath());
            Files.deleteIfExists(new File("diff_" + model.dbName).toPath());
            if (!Files.exists(Paths.get(model.folderName))) {
                Files.createDirectory(new File(model.folderName).toPath());
            }
        } catch (IOException e) {
            Logger.err("Can not prepare files");
            e.printStackTrace();
        }
    }

    /**
     * Open temp and diff connections, create tables
     *
     * @param model Model of data in the database
     */
    private static void initConnections(DataModel model) {
        try {
            tempConn = DriverManager.getConnection("jdbc:sqlite:temp_" + model.dbName);
            currConn = DriverManager.getConnection(
                    "jdbc:sqlite:" + model.folderName + "/" + model.dbName);

            createTablesIfNeeded(model);
        } catch (SQLException e) {
            Logger.err("Can not initialize connections");
            e.printStackTrace();
        }
    }

    private static void closeConnections() {
        try {
            closeIfNeeded(tempConn);
            closeIfNeeded(diffConn);
            closeIfNeeded(currConn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTablesIfNeeded(DataModel model) throws SQLException {
        String schema = DBUtil.getCreateTableSchema(model);
        try (Statement tr = currConn.createStatement()) {
            if (!currConn.getMetaData()
                    .getTables(null, null, TABLE_NAME, null).next()) {
                tr.execute(schema);
            }
        }
        diffConn = DriverManager.getConnection("jdbc:sqlite:diff_" + model.dbName);
        try (Statement s1 = tempConn.createStatement();
             Statement s2 = diffConn.createStatement()) {
            s1.execute(schema);
            s2.execute(schema);
        }
    }

    /**
     * Update versions and databases.
     * Data is being copied from the temp_ files.
     */
    private static void commitChanges() {
        try {
            updateVersions();
            updateDatabases();
            updateVersionFile();
        } catch (IOException e) {
            Logger.err("Can not commit changes after updating");
            e.printStackTrace();
        }
    }

    public static File getVersionFile() {
        if (versionFile == null) {
            try {
                readVersionFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return versionFile;
    }

    private static void updateVersions() {
        try (BufferedReader in = new BufferedReader(new FileReader(getVersionFile()))) {
            versions = new int[CAT_NUMBER];
            for (int i = 0; i < CAT_NUMBER; i++) {
                try {
                    versions[i] = Integer.parseInt(in.readLine());
                } catch (Exception e) {
                    versions[i] = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readVersionFile() throws IOException {
        versionFile = new File(VERSION_FILE_NAME);
        if (!versionFile.exists()) {
            if (!versionFile.createNewFile()) {
                throw new IOException("Couldn't create version file");
            }
            initVersionFile();
        }
    }

    private static void initVersionFile() {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(versionFile))) {
            String s = "";
            for (int i = 0; i < CAT_NUMBER; i++) {
                s += "0\n";
            }
            out.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateVersionFile() throws IOException {
        Files.delete(getVersionFile().toPath());
        Files.createFile(getVersionFile().toPath());
        PrintWriter out = new PrintWriter(versionFile);
        for (int i = 0; i < CAT_NUMBER; i++) {
            out.println(versions[i]);
        }
        out.close();
    }

    /**
     * Copy databases from temporary files to final folders.
     */
    private static void updateDatabases() {
        try {
            for (Source source : DataUpdater.SOURCES) {
                DataModel model = DataModel.read(source);
                versions[model.id]++;
                Files.copy(new File("temp_" + model.dbName).toPath(),
                        new File(model.folderName + "/" + versions[model.id] + ".db").toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                Files.copy(new File("temp_" + model.dbName).toPath(),
                        new File(model.folderName + "/" + model.dbName).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
