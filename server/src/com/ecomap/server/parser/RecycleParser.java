package com.ecomap.server.parser;

import com.ecomap.server.util.Logger;
import com.ecomap.server.util.NetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class RecycleParser {

    private static final int DEFAULT_MAX_ID = 16000, DEFAULT_THRESHOLD = 600;

    private static String CONFIG_FILENAME = "config/iters.conf";

    private static int MAX_ID, THRESHOLD;

    public static List<Place> loadPlaces() {
        readConfig();
        List<Place> result = new ArrayList<>();
        int emptyIds = 0;
        for (int i = 1; i < MAX_ID; i++) {
            try {
                RecyclePlace place = loadRecyclePlace(i);
                if (place == null) {
                    emptyIds++;
                } else {
                    emptyIds = 0;
                    result.add(place);
                }
            } catch (IOException e) {
                Logger.err("Couldn't connect to recycle");
                e.printStackTrace();
            } catch (Exception e) {
                Logger.err("Something unexpected");
                e.printStackTrace();
            }
            if (emptyIds > THRESHOLD) {
                break;
            }
        }

        return result;
    }

    private static void readConfig() {
        try {
            Scanner in = new Scanner(new File(CONFIG_FILENAME));
            MAX_ID = in.nextInt();
            THRESHOLD = in.nextInt();
        } catch (FileNotFoundException e) {
            Logger.err("Can not read recycle handler config files");
            MAX_ID = DEFAULT_MAX_ID;
            THRESHOLD = DEFAULT_THRESHOLD;
        }
    }

    /**
     * Parse recycle place with particular id
     *
     * @param id Id of wanted recycle place
     * @return Parsed recycle place or null if it does not exist
     * @throws IOException
     */
    private static RecyclePlace loadRecyclePlace(int id) throws IOException {
        RecyclePlace place = new RecyclePlace(id);
        List<String> page = getRecyclePage(id);
        String t;
        for (int i = 0; i < page.size(); i++) {
            String s = page.get(i);
            if (s.contains("point_head")) {
                i += 2;
                s = page.get(i);
                t = s.substring(s.indexOf("<span>") + 6);
                place.title = t.substring(0, t.indexOf("</span>"));
            } else if (s.contains("data-lat=")) {
                t = s.substring(s.indexOf("lat=\"") + 5);
                if (t.charAt(0) == '\"')
                    return null;
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
            } else if (s.contains("=\"trash_type")) {
                place.content_text = "";
                for (String x : RecyclePlace.categoryNames) {
                    if (s.contains(x)) {
                        place.content_text += x + ", ";
                    }
                }
                if (s.length() >= 2) {
                    place.content_text = place.content_text.substring(0, place.content_text.length() - 2);
                }
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
        return place;
    }

    private static List<String> getRecyclePage(int id) throws IOException {
        String url = "http://recyclemap.ru/index.php?task=infopoint&pointid=" + id + "&tmpl=component#";
        return NetUtil.readPage(url);
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
        String t = s;
        try {
            while (s.contains("href")) {
                String pref = s.substring(0, s.indexOf("<a "));
                String suff = s.substring(s.indexOf("</a>") + 4);
                String ref = s.substring(s.indexOf("<a "), s.indexOf("</a>") + 4);
                ref = ref.substring(ref.indexOf("href=\"") + 6, ref.indexOf("\">"));
                s = pref + suff + ref;
            }
        } catch (Exception e) {
            Logger.log("Couldn't parse reference.");
            s = t;
        }
        return s;
    }
}
