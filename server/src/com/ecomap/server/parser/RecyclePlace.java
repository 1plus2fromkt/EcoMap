package com.ecomap.server.parser;

import com.ecomap.server.DataModel;
import com.ecomap.server.Source;
import com.ecomap.server.db.DBUtil;
import com.ecomap.server.util.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.ecomap.server.util.TextUtil.removeIllegalSymbols;

class RecyclePlace extends Place {

    int id;
    double lat, lng, rate;
    String content_text = "", address, title, img_link, info = "",
            site = "", work_time = "", telephone = "", e_mail = "";
    static final String[] categoryNames = {"Бумага", "Стекло", "Пластик", "Металл", "Одежда",
            "Иное", "Опасные отходы", "Батарейки", "Лампочки", "Бытовая техника", "Тетра Пак"};

    RecyclePlace(int id) {
        this.id = id;
    }

    @Override
    public void writeToDB(Connection destConn) {
        String sep = "', '";
        try (Statement st = destConn.createStatement()) {
            prepareStringsForSQL();
            String log = "\'" + id + sep + lat + sep + lng + sep + rate + sep +
                    title + sep + content_text + sep + address + sep + img_link + sep + info +
                    sep + work_time + sep + site + sep + telephone + sep + e_mail + "\'";
            String schema = DBUtil.getInsertSchema(DataModel.read(Source.RECYCLE), log, false);
            st.execute(schema);
        } catch (SQLException e) {
            Logger.err("Can not write place to database");
            e.printStackTrace();
        }
    }

    private void prepareStringsForSQL() {
        address = removeIllegalSymbols(address);
        title = removeIllegalSymbols(title);
        info = removeIllegalSymbols(info);
        site = removeIllegalSymbols(site);
        telephone = removeIllegalSymbols(telephone);
        e_mail = removeIllegalSymbols(e_mail);
        content_text = removeIllegalSymbols(content_text);
    }

}
