package com.ecomap.server.parser;

import com.ecomap.server.util.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import static com.ecomap.server.util.NetUtil.readPage;

class EcomobileParser {
    static final String url = "http://ecomobile.infoeco.ru/grafik-stoyanok.html";

    static List<Place> loadPlaces() {
        List<String> page = readPage(url);
        List<EcomobilePlace> result = parsePage(page);
        merge(result);
        List<Place> finalRes = new ArrayList<>();
        result.forEach(finalRes::add);
        return finalRes;
    }

    private static void merge(List<EcomobilePlace> data) {
        HashSet<String> seenAddresses = new HashSet<>();
        List<EcomobilePlace> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            EcomobilePlace curr = data.get(i);
            String currAddress = curr.address;
            if (seenAddresses.contains(currAddress)) {
                continue;
            }
            for (int j = i + 1; j < data.size(); j++) {
                if (data.get(j).address.equals(currAddress)) {
                    curr.addAll(data.get(j).timetable);
                }
            }
            seenAddresses.add(currAddress);
            result.add(curr);
        }
        data.clear();
        data.addAll(result);
    }

    private static List<EcomobilePlace> parsePage(List<String> page) {
        List<EcomobilePlace> result = new ArrayList<>();
        int p = 0;
        while (!"<h2>Расписание экомобиля</h2>".equals(page.get(p++))) {}
        p++;
        while (page.get(p).equals("<tr>")) {
            p++;
            Function<String, String> trimTags = s -> {
                int from = 0, to = s.length() - 1;
                try {
                    while (s.charAt(from) != '>') from++;
                    while (s.charAt(to) != '<') to--;
                } catch (IndexOutOfBoundsException e) {
                    Logger.log("Trimmer error: "  + s + " doesn't have tags");
                    return "Error";
                }
                return s.substring(from + 1, to);
            };
            String district = trimTags.apply(page.get(p++));
            String address = trimTags.apply(page.get(p++));
            String date = trimTags.apply(page.get(p).substring(0, page.get(p).indexOf("><") + 1));
            String time = trimTags.apply(page.get(p).substring(page.get(p).indexOf("><") + 1));
            district = district.replaceAll("[\'\"]", "");
            address = address.replaceAll("[\'\"]", "");

            result.add(new EcomobilePlace(district, address, date, time));

            while (!"</tr>".equals(page.get(p++))) {}
        }

        return result;
    }

    public static class DateTime {
        String date;
        String time;

        DateTime(String date, String time) {
            this.date = date;
            this.time = time;
        }

        public boolean equals(Object o) {
            return o instanceof DateTime && equals((DateTime) o);
        }

        private boolean equals(DateTime d) {
            return d != null && date.equals(d.date) && time.equals(d.time);
        }

        public int hashCode() {
            return date.hashCode() * 9842 + time.hashCode() * 675982;
        }

        public String toString() {
            return date + " " + time;
        }
    }
}
