package places.Recycle;

import java.io.IOException;
import java.util.ArrayList;

import static places.Recycle.RecyclePlace.categoryNames;
import static places.Recycle.RecyclePlace.getPageInfo;

class RecycleParser {

    static boolean readInfoRecycle(RecyclePlace place) throws IOException {
        ArrayList<String> page = getPageInfo(place.id);
        String t;
        for (int i = 0; i < page.size(); i++) {
            String s = page.get(i);
            if(s.contains("point_head")) {
                i += 2;
                s = page.get(i);
                t = s.substring(s.indexOf("<span>") + 6);
                place.title = t.substring(0, t.indexOf("</span>"));
            } else if (s.contains("data-lat=")) {
                t = s.substring(s.indexOf("lat=\"") + 5);
                if (t.charAt(0) == '\"')
                    return false;
                place.lat = Double.parseDouble(t.substring(0, t.indexOf("\"")));
                t = s.substring(s.indexOf("lng=\"") + 5);
                place.lng = Double.parseDouble(t.substring(0, t.indexOf("\"")));
            } else if (s.contains("point_image")) {
                i++;
                s = page.get(i);
                while (s.contains("href")) {
                    place.img_link = "";
                    s = s.substring(s.indexOf("href=\"") + 6);
                    t = s.substring(0, s.indexOf('\"'));
                    if (t.contains("imgs.jpg")) {
                        place.img_link = "Default";
                    } else {
                        place.img_link += t + ", ";
                    }
                }
            } else if (s.contains("point_address")) {
                i++;
                s = page.get(i);
                place.address = s.replace("</div>", "").trim();
            } else if (s.contains("point_reiting_val")) {
                t = s.substring(s.indexOf("\">") + 2);
                place.rate = Double.parseDouble(t.substring(0, t.indexOf("</")));
            } else if(s.contains("=\"trash_type")) {
                place.content_text = "";
                for (String x : categoryNames) {
                    if (s.contains(x)) {
                        place.content_text += x + ", ";
                    }
                }
                if (s.length() >= 2)
                    place.content_text = place.content_text.substring(0, place.content_text.length() - 2);
            } else if (s.contains(">Общая информация</a>")) {
                i += 3;
                s = page.get(i);
                for (; !s.contains("</div>"); s = page.get(++i)) {
                    s = s.replace("<br />", " ").replace("<br/>", " ").trim() + " ";
                    place.info += getRef(s);
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
        return true;
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

    private static String getRef(String s) {
        while (s.contains("href")) {
            String pref = s.substring(0, s.indexOf("<a "));
            String suff = s.substring(s.indexOf("</a>") + 4);
            String ref = s.substring(s.indexOf("<a "), s.indexOf("</a>") + 4);
            ref = ref.substring(ref.indexOf("href=\"") + 6, ref.indexOf("\">"));
            s = pref + suff + ref;
        }
        return s;
    }
}
