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

    String address;
    String district;
    double lat, lng;
    List<EcomobileParser.DateTime> timetable;

    EcomobilePlace(String district, double lat, double lng, String address, String date, String time) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.district = district;
        timetable = new ArrayList<>();
        timetable.add(new EcomobileParser.DateTime(date, time));
    }

    @Override
    public void writeToDB(Connection destConn) {
        String sep = "\', \'";
        try (Statement st = destConn.createStatement()) {
            String timetableString = "";
            for (EcomobileParser.DateTime dt : timetable) {
                timetableString += dt + " | ";
            }
            String log = "\'" + address + sep + lat + sep + lng + sep + district + sep + timetableString + "\'";
            String schema = DBUtil.getInsertSchema(DataModel.read(Source.ECOMOBILE), log, false);
            st.execute(schema);
        } catch (SQLException e) {
            Logger.err("Can not write place " + this + " to database");
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
