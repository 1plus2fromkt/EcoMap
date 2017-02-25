package com.ecomap.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetUtil {

    public static List<String> readPage(String address) {
        List<String> ans = new ArrayList<>();
        try {
            URL url = new URL(address);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                ans.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

}
