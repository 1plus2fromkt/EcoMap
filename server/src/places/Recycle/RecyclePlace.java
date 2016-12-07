package places.Recycle;

import db.DBAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static db.DataUpdater.TRASH_N;

class RecyclePlace {

    int id, NUM = TRASH_N;
    double lat, lng, rate;
    String content_text, address, title, img_link, info = "", site = "", work_time = "", telephone = "", e_mail = "";

    static ArrayList<String> getPageInfo(int id) {
        ArrayList<String> ans = new ArrayList<>();
        try {
            URL url = new URL("http://recyclemap.ru/index.php?task=infopoint&pointid=" + id +
                    "&tmpl=component#");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "URL-8"));
            String s;
            while ((s = in.readLine()) != null) {
                ans.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    void writeToDB(Connection conn) throws SQLException {
        String sep = "', '";
        try (Statement st = conn.createStatement()) {
            RecycleParser.readInfoRecycle(this);
            prepareStringsForSQL();
            String log = "\'" + id + sep + lat + sep + lng + sep + rate + sep +
                    title + sep + content_text + sep + address + sep + img_link + sep + info +
                    sep + work_time + sep + site + sep + telephone + sep + e_mail + "\'";
            String sch = DBAdapter.getInsertSchema(NUM, log, false);
            st.execute(sch);
            System.out.println(log);
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

    private String removeIllegalSymbols(String s) {
        return s.replaceAll("[\']", "");
    }

}
