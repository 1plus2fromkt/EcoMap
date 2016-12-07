package places.Recycle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class RecycleHandler {


    static final String[] tags = {"id", "lat", "lng", "title", "reiting", "content_text", "address"};
    private static final String basicURL = "http://recyclemap.ru/";
    private static ArrayList<Integer> cityIds = new ArrayList<>();

    public static void updateData(Connection c) throws IOException {
        getCities(getSourceOfCity(37)); // universal constant
        cityIds.add(37); // Recyclemap doesn't give id of the curr city
        if (cityIds.size() <= 1) {
            throw new IOException("Couldn't parse cities");
        }
        int cnt = 0;
        try {
            for (int i : cityIds) {
                try {
                    RecycleParser.addPlaces(getSourceOfCity(i), c);
                } catch (IOException e) {
                    System.out.println("Couldn't get page of city " + i);
                    e.printStackTrace();
                }
                System.out.println("Updated trash for city " + i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> getSourceOfCity(int i) throws IOException {
        ArrayList<String> s = new ArrayList<>();
        URL url = new URL(basicURL + "?city=" + i);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        String t;
        while ((t = in.readLine()) != null)
            s.add(t);
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
