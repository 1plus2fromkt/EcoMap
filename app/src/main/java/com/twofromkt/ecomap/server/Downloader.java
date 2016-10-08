package com.twofromkt.ecomap.server;

import com.twofromkt.ecomap.data_struct.Pair;

import java.util.ArrayList;

public class Downloader {
    //this should connect to server and update database. but later

    public static ArrayList<Pair<Double, Double>> data;

    static {
        data = new ArrayList<Pair<Double, Double>>() {{
            add(new Pair<>(60.043175, 30.409615));
            add(new Pair<>(60.003030682141, 30.3609641822784));
            add(new Pair<>(59.9727646, 30.3396640999999));
        }};
    }

}
