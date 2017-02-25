package com.ecomap.server;

import com.google.gson.Gson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.sqlite.JDBC");
        DataModel model = DataModel.read(Source.ECOMOBILE);

//        Connection oldConn = DriverManager.getConnection("jdbc:sqlite:ecom.db");
//        Connection newConn = DriverManager.getConnection("jdbc:sqlite:ecom2.db");
//        Connection diffConn = DriverManager.getConnection("jdbc:sqlite:ecomdiff.db");
//        DataUpdater.getDifference(oldConn, newConn, diffConn, model);
//        diffConn.close();

//        String s = "{\n" +
//                "    \"p_result\": \"ok\",\n" +
//                "    \"p_item\": [\n" +
//                "        {\n" +
//                "            \"p_id\": 132,\n" +
//                "            \"p_name\": \"Николай\"\n" +
//                "        }, \n" +
//                "        {\n" +
//                "            \"p_id\": 133,\n" +
//                "            \"p_name\": \"Светлана\"\n" +
//                "        }\n" +
//                "    ]\n" +
//                " }";
//        JsonParser parser = new JsonParser();
//        JsonObject obj = parser.parse(s).getAsJsonObject();
//        JsonArray arr = obj.getAsJsonArray("p_item");
//        for (JsonElement e : arr) {
//            JsonObject currObj = e.getAsJsonObject();
//            System.out.println(currObj.get("p_id"));
//            System.out.println(currObj.get("p_name"));
//        }

//        JsonReader r = new JsonReader(new InputStreamReader(System.in));
        Info info = new Info();
        info.data = new ArrayList<>();
        info.data.add(new Pair<>("key", 1));
        Gson g = new Gson();
        String s = g.toJson(info);
        System.out.println(s);
    }

    static class Info {
        List<Pair<String, Integer>> data;
    }

}