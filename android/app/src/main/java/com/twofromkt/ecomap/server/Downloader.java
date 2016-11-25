package com.twofromkt.ecomap.server;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.twofromkt.ecomap.data_struct.Pair;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Downloader {
    //this should connect to server and update database. but later

    public static ArrayList<Pair<Double, Double>> data;

    private static final String versionFileName = "version.txt", diffPath = "diff/",
                            dbPath = "db/";

    public static void update(Context context) throws IOException {
        ArrayList<Boolean> toUpdate = download(context);
        for (int i = 0; i < toUpdate.size(); i++) {
            if (toUpdate.get(i))
                replace(i);
        }
    }

    private static void replace (int i) {
//        try (SQLiteDatabase curr = SQLiteDatabase.openDatabase()){
//
//        }
    }

    private static ArrayList<Boolean> download(Context context) throws IOException {
        String server = "37.46.133.69";
        Socket socket = new Socket(server, 4444);
        socket.setTcpNoDelay(true);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ArrayList<Integer> versions = getVersions(context);
        for (int i : versions)
            out.write(i);
        out.write(-1);
        int i = 0, version;
        ArrayList<Boolean> ans = new ArrayList<>();
        while ((version = in.readInt()) != -1) {
            File f = new File(context.getFilesDir(), diffPath + "diff" + i + ".db");
            if (version == 1) {
                FileOutputStream fileOut = new FileOutputStream(f);
                long fileSize = in.readLong();
                byte[] buffer = new byte[1024 * 8];
                int cnt;
                while (fileSize > 0 && (cnt = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1)
                {
                    fileOut.write(buffer, 0, cnt);
                    fileSize -= cnt;
                }
                fileOut.close();
                ans.add(true);
            }
            else if (version == 0) {
                ans.add(false);
            } else if (version < 0) {
                ans.add(false);
                System.err.println("ALERT! Error answer " + version);
            }
            i++;
        }
        updateVersionFile(versions, ans, context);
        return ans;
    }

    private static ArrayList<Integer> getVersions(Context context) throws IOException {
        File ver = new File(context.getFilesDir(), versionFileName);
        if (!ver.exists()) {
            return new ArrayList<>();
        }
        BufferedReader in = new BufferedReader(new FileReader(ver));
        String s;
        ArrayList<Integer> ans = new ArrayList<>();
        while ((s = in.readLine()) != null) {
            ans.add(Integer.parseInt(s));
        }
        in.close();
        return ans;
    }

    private static void updateVersionFile(ArrayList<Integer> prevVersions,
                                          ArrayList<Boolean> isUpdated,
                                          Context context) {
        try {
            File ver = new File(context.getFilesDir(), versionFileName);
            PrintWriter out = new PrintWriter(ver);
            if (ver.exists())
                ver.delete();
            ver.createNewFile();
            for (int i = 0; i < isUpdated.size(); i++) {
                if (prevVersions.size() <= i)
                    out.println(1);
                else
                    out.println(prevVersions.get(i) + (isUpdated.get(i) ? 1 : 0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
