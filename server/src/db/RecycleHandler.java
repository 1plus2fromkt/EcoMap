package db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class RecycleHandler {


    private static ArrayList<Integer> cityIds = new ArrayList<>();
    private static final String basicURL = "http://recyclemap.ru/";

    static final String[] tags = {"id", "lat", "lng", "title", "reiting", "content_text", "address"};

    public static void updateData(Connection c) {
        getCities(getSourceOfCity(37)); // universal constant
        cityIds.add(37); // Recyclemap doesn't give id of the curr city
        int cnt = 0;
        try {
            for (int i : cityIds) {
                addPlaces(getSourceOfCity(i), c);
                System.out.println(cnt++);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addPlaces(ArrayList<String> page, Connection connection) throws SQLException {
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
                    s = s.substring(s.indexOf(":") + 1).trim();
                    s = s.substring(0, s.length() - 1).trim();
                    switch (curr) {
                        case 0: pl.id = Integer.parseInt(s); break;
                        case 1: pl.lat = Double.parseDouble(s); break;
                        case 2: pl.lng = Double.parseDouble(s); break;
                        case 3: pl.title = s; break;
                        case 4: pl.rate = Double.parseDouble(s); break;
                        case 5: pl.content_text = s; break;
                        case 6: pl.address = s; break;
                    }
                    curr++;
                }
            }
            pl.wrightToDB(connection);
            cnt++;
            System.out.println(cnt + " " + pl.id);
            if (s.contains("}};"))
                return;
        }
    }

    private static ArrayList<String> getSourceOfCity(int i) {
        ArrayList<String> s = new ArrayList<>();
        try {
            URL url = new URL(basicURL + "?city=" + Integer.toString(i));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String t;
            while ((t = in.readLine()) != null)
                s.add(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    private static void getCities(ArrayList<String> page) {
        boolean start = false;
        for (String s : page) {
            if (s.contains("var cities")) {
                start = true;
                continue;
            }
            if (!start)
                continue;
            if (s.contains("id")) {
                s = s.substring(s.indexOf(':') + 1).trim();
                cityIds.add(Integer.parseInt(s.substring(0, s.length() - 1).trim()));
            }
            if (s.contains("}}"))
                break;
        }
    }



}
