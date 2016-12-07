package places.Recycle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static places.Recycle.RecycleHandler.tags;
import static places.Recycle.RecyclePlace.getPageInfo;

class RecycleParser {
    static void addPlaces(ArrayList<String> page, Connection connection) throws SQLException {
        boolean start = false;
        int cnt = 0;
        for (int i = 0; i < page.size(); i++) {
            String s = page.get(i);
            if (s.contains("var point_info")) {
                start = true;
                continue;
            }
            if (!start)
                continue;
            int curr = 0;
            RecyclePlace pl = new RecyclePlace();
            for (; !s.contains("},") && !s.contains("}};"); s = page.get(i++)) {
                if (curr < tags.length && s.contains(tags[curr])) {
                    parseString(curr, pl, s);
                    curr++;
                }
            }
            pl.writeToDB(connection);
            cnt++;
            System.out.println(cnt + " " + pl.id);
            if (s.contains("}};"))
                return;
        }
    }

    private static void parseString(int curr, RecyclePlace pl, String s) {
        s = s.substring(s.indexOf(":") + 1).trim();
        s = s.substring(0, s.length() - 1).trim();
        switch (curr) {
            case 0:
                pl.id = Integer.parseInt(s);
                break;
            case 1:
                pl.lat = Double.parseDouble(s);
                break;
            case 2:
                pl.lng = Double.parseDouble(s);
                break;
            case 3:
                pl.title = s;
                break;
            case 4:
                pl.rate = Double.parseDouble(s);
                break;
            case 5:
                pl.content_text = s;
                break;
            case 6:
                pl.address = s;
                break;
        }
    }

    static void readInfoRecycle(RecyclePlace place) {
        ArrayList<String> page = getPageInfo(place.id);
        for (int i = 0; i < page.size(); i++) {
            String s = page.get(i);
            if (s.contains("point_image")) {
                i++;
                s = page.get(i);
                s = s.substring(s.indexOf('\"') + 1);
                place.img_link = s.substring(0, s.indexOf('\"'));
                if (place.img_link.contains("imgs.jpg"))
                    place.img_link = "Default";
            } else if (s.contains(">Общая информация</a>")) {
                i += 3;
                s = page.get(i);
                for (; !s.contains("</div>"); s = page.get(++i)) {
                    s = s.replace("<br />", " ").replace("<br/>", " ").trim() + " ";
                    place.info += s;
                }
                place.info += s.replace("</div>", " ").trim();
            } else if (s.contains(">Время работы</a>")) {
                i += 3;
                s = page.get(i);
                place.work_time = "0 " + s.replace("</div>", " ").trim(); // 0_ if it is string, not a timetable
            } else if (s.contains("time_schem")) {
                i += 4;
                s = page.get(i);
                getTime(s, place);
            } else if (s.contains(">Контакты</div>")) {
                getContacts(page.get(++i), place);
            } else if (s.contains(">Круглосуточно<")) {
                place.work_time = "0 Круглосуточно";
            }

        }
    }

    private static void getTime(String s, RecyclePlace place) {
        s = s.replace(" today", "");
        String[] a = s.split("[<>]");
        for (int i = 0; i < a.length; i++) {
            s = a[i];
            if (s.contains("td class=\"\"")) {
                place.work_time += a[++i] + "#" + a[i += 2] + "$";
            } else if (s.contains("td class=\"holiday\"")) {
                place.work_time += "h$";
            }
        }
        place.work_time = place.work_time.substring(0, place.work_time.length() - 1);
    }

    private static void getContacts(String s, RecyclePlace place) {
        String[] a = s.split("[<>]");
        for (int i = 0; i < a.length; i++) {
            if (a[i].contains("phone")) {
                i++;
                place.telephone += a[i] + "; ";
            } else if (a[i].contains("email")) {
                i++;
                place.e_mail += a[i] + "; ";
            } else if (a[i].contains("site")) {
                i++;
                place.site += a[i] + "; ";
            }
        }
    }
}
