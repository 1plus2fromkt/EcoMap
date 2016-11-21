package places.Recycle;

import db.DataUpdator9000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static db.DataUpdator9000.TRASH_N;

class RecyclePlace {

    int id, NUM = TRASH_N;
    double lat, lng, rate;
    String content_text, address, title, img_link, info = "", site = "", work_time = "", telephone = "", e_mail = "";

    void writeToDB(Connection conn) throws SQLException {
        String sep = "', '";
        Statement st = conn.createStatement();
        readInfoRecycle();
        deleteA();
        if (id == 1 || id == 2 || id == 3) {
            address += "pyathuizov";
        }
        String log = "\'" + id + sep + lat + sep + lng + sep + rate + sep +
                content_text + sep + address+ sep + title+ sep + img_link + sep + title +
                sep + work_time + sep + site + sep + telephone + sep + e_mail + "\'";
        String sch = DataUpdator9000.getInsertScheme(NUM, log, false);
        st.execute(sch);
        System.out.println(log);
    }

    private void deleteA() {
        address = address.replaceAll("[\'\"]", "");
        title = title.replaceAll("[\'\"]", "");
        info = info.replaceAll("[\'\"]", "");
        site = site.replaceAll("[\'\"]", "");
        telephone = telephone.replaceAll("[\'\"]", "");
        e_mail = e_mail.replaceAll("[\'\"]", "");
        content_text = content_text.replaceAll("[\'\"]", "");
    }

    private void readInfoRecycle() {
        return;/*
        ArrayList<String> page = getPageInfo(id);
        for (int i = 0; i < page.size(); i++) {
            String s = page.get(i);
            if (s.contains("point_image")) {
                i++;
                s = page.get(i);
                s =  s.substring(s.indexOf('\"') + 1);
                img_link = s.substring(0, s.indexOf('\"'));
                if (img_link.contains("imgs.jpg"))
                    img_link = "Default";
            } else if (s.contains(">Общая информация</a>")) {
                i += 3;
                s = page.get(i);
                for (; !s.contains("</div>"); s = page.get(++i)) {
                    s = s.replace("<br />", " ").replace("<br/>", " ").trim() + " ";
                    info += s;
                }
                info += s.replace("</div>", " ").trim();
            } else if (s.contains(">Время работы</a>")) {
                i += 3;
                s = page.get(i);
                work_time = "0 " + s.replace("</div>", " ").trim(); // 0 if it is string, not a timetable
            } else if (s.contains("time_schem")) {
                i += 4;
                s = page.get(i);
                getTime(s);
//                work_time = "1 " + s.replace("</td><td class=\"\">", "$").replace("<td class=\"\">", " ")
//                        .replace("<br />", ">").replace("</td>", "").replace("</tr>", "").trim();
            } else if (s.contains(">Контакты</div>")) {
                getContacts(page.get(++i));
            }

        }*/
    }

    private void getTime(String s) {
        s = s.replace(" today", "");
        String[] a = s.split("[<>]");
        for (int i = 0; i < a.length; i++) {
            s = a[i];
            if (s.contains("td class=\"\"")) {
                work_time += a[++i] + "#" + a[i += 2] + "$";
            } else if (s.contains("td class=\"holiday\"")) {
                work_time += "h$";
            }
        }
        work_time = work_time.substring(0, work_time.length() - 1);
    }

    private void getContacts(String s) {
        String[] a = s.split("[<>]");
        for (int i = 0; i < a.length; i++) {
            if (a[i].contains("phone")) {
                i++;
                telephone += a[i] + "; ";
            } else if (a[i].contains("email")) {
                i++;
                e_mail += a[i] + "; ";
            } else if(a[i].contains("site")) {
                i++;
                site += a[i] + "; ";
            }
        }
    }


    private static ArrayList<String> getPageInfo(int id) {
        ArrayList<String> ans = new ArrayList<>();
        try {
            URL url = new URL("http://recyclemap.ru/index.php?task=infopoint&pointid=" + id +
                "&tmpl=component#");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String s;
            while ((s = in.readLine()) != null) {
                ans.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

}
