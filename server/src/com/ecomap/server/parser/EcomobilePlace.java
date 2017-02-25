package com.ecomap.server.parser;

import com.ecomap.server.DataModel;
import com.ecomap.server.Source;
import com.ecomap.server.db.DBUtil;
import com.ecomap.server.util.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class EcomobilePlace extends Place {

    String district;
    String address;
    List<EcomobileParser.DateTime> timetable;

    EcomobilePlace(String district, String address, String date, String time) {
        this.district = district;
        this.address = address;
        timetable = new ArrayList<>();
        timetable.add(new EcomobileParser.DateTime(date, time));
    }

    @Override
    public void writeToDB(Connection destConn) {
        String sep = "', '";
        try (Statement st = destConn.createStatement()) {
//            prepareStringsForSQL();
            String timetableString = "";
            for (EcomobileParser.DateTime dt : timetable) {
                timetableString += dt + " | ";
            }
            String log = "\'" + district + sep + address + sep + timetableString + "\'";
            String schema = DBUtil.getInsertSchema(DataModel.read(Source.RECYCLE), log, false);
            st.execute(schema);
        } catch (SQLException e) {
            Logger.err("Can not write place to database");
            e.printStackTrace();
        }
    }

    void add(EcomobileParser.DateTime dt) {
        timetable.add(dt);
    }

    void addAll(Collection<EcomobileParser.DateTime> dateTimes) {
        timetable.addAll(dateTimes);
    }

    public boolean equals(Object o) {
        return o instanceof EcomobilePlace && equals((EcomobilePlace) o);
    }

    private boolean equals(EcomobilePlace e) {
        return e != null && district.equals(e.district) && address.equals(e.address)
                && timetable.equals(e.timetable);
    }

    public int hashCode() {
        return district.hashCode() * 6734 + address.hashCode() * 8626 + timetable.hashCode() * 6752;
    }

    public String toString() {
        String s = "Ecomobile place:\n" + district + "\n" + address + "\n" + timetable.size() + " dates\n";
        for (EcomobileParser.DateTime dt : timetable) {
            s += dt + "\n";
        }
        return s;
    }

}
