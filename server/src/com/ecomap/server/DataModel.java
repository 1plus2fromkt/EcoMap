package com.ecomap.server;

import com.ecomap.server.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class describes a data model for one of our data sources.
 * It contains info about structure of data, names of files connected
 * with it etc.
 * Structure of .conf file is
 *
 * name of database file
 * name of folder with databases
 * id of the source type
 * names of tabs
 * names of types
 */
public class DataModel {
    public final String dbName;
    public final String folderName;
    public final int id;
    public final List<Entry> fields;

    private DataModel(String filename) {
        Scanner in;
        try {
            in = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            Logger.err("Can not read config file " + filename);
            dbName = folderName = "";
            fields = new ArrayList<>();
            id = -1;
            return;
        }
        dbName = in.next();
        folderName = in.next();
        id = in.nextInt();
        fields = new ArrayList<>();
        while (in.hasNext()) {
            String name = in.next();
            String type = in.nextLine().trim();
            fields.add(new Entry(name, type));
        }
    }

    private DataModel() {
        dbName = folderName = "";
        fields = new ArrayList<>();
        id = -1;
    }

    public static DataModel read(Source source) {
        switch (source) {
            case RECYCLE:
                return new DataModel("config/recycle.conf");
            case ECOMOBILE:
                return new DataModel("config/ecomobile.conf");
            default:
                Logger.err("Unknown source type " + source);
                return new DataModel();
        }
    }

    public String toString() {
        return dbName;
    }

    public static class Entry {
        public final String name;
        public final String type;

        Entry(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public boolean equals(Object o) {
            return o instanceof Entry && equals((Entry) o);
        }

        private boolean equals(Entry e) {
            return e != null && name.equals(e.name) && type.equals(e.type);
        }

        public int hashCode() {
            return name.hashCode() * 9842 + type.hashCode() * 675982;
        }

        public String toString() {
            return name + " : " + type;
        }
    }
}
