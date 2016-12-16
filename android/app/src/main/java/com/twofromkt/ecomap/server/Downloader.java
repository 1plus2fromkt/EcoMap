package com.twofromkt.ecomap.server;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.twofromkt.ecomap.data_struct.Pair;
import com.twofromkt.ecomap.db.DBAdapter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Downloader {
    public static ArrayList<Pair<Double, Double>> data;

    private static final String VERSION_FILE_NAME = "version.txt";
    private static final String SERVER_IP = "37.46.133.69";

    public static ServerResultType update(Context context) {
        Pair<ArrayList<Boolean>, ArrayList<Integer>> toUpdate;
        try {
            toUpdate = download(context);
        } catch (IOException e) {
            e.printStackTrace();
            return new ServerResultType(false);
        }

        for (int i = 0; i < toUpdate.val1.size(); i++) {
            if (toUpdate.val1.get(i)) {
                DBAdapter.replace(i, context);
            }
        }
        updateVersionFile(toUpdate.val2, context);
        return new ServerResultType(true);
    }

    private static Pair<ArrayList<Boolean>, ArrayList<Integer>> download(Context context) throws IOException {
        Log.d("DOWNLOADER", "starting download");
        Socket socket = new Socket(SERVER_IP, 4444);
        socket.setTcpNoDelay(true);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ArrayList<Integer> versions = getVersions(context);
        for (int i : versions)
            out.writeInt(i);
        out.writeInt(-1);
        out.flush();
        int i = 0, version;
        new File(context.getFilesDir(), DBAdapter.getDiffPath()).mkdir();
        ArrayList<Boolean> ans = new ArrayList<>();
        ArrayList<Integer> currVers = new ArrayList<>();
        while ((version = in.readInt()) != -1) {
            File f = new File(context.getFilesDir(), DBAdapter.getDiffPath() + "diff" + i + ".db");
            f.createNewFile();
            if (version == 1) {
                try (FileOutputStream fileOut = new FileOutputStream(f)) {
                    currVers.add(in.readInt());
                    long fileSize = in.readLong();
                    byte[] buffer = new byte[1024 * 8];
                    int cnt;
                    while (fileSize > 0 && (cnt = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                        fileOut.write(buffer, 0, cnt);
                        fileSize -= cnt;
                    }
                    fileOut.close();
                    ans.add(true);
                }
            }
            else if (version == 0) {
                currVers.add(versions.get(i));
                ans.add(false);
            } else if (version < 0) {
                currVers.add(versions.get(i));
                ans.add(false);
                System.err.println("ALERT! Error answer " + version);
            }
            i++;
        }
        Log.d("downloader", "finished download");
        return new Pair<>(ans, currVers);
    }

    private static ArrayList<Integer> getVersions(Context context) throws IOException {
        File ver = new File(context.getFilesDir(), VERSION_FILE_NAME);
        if (!ver.exists()) {
            return new ArrayList<>();
        }
        ArrayList<Integer> ans = new ArrayList<>();
        try (DataInputStream in = new DataInputStream(new FileInputStream(ver))) {
            int s;
            try {
                while (true) {
                    s = in.readInt();
                    ans.add(s);
                }
            } catch (EOFException ignored) {

            }
        }
        return ans;
    }

    private static void updateVersionFile(ArrayList<Integer> prevVersions,
                                          Context context) {
        File ver = new File(context.getFilesDir(), VERSION_FILE_NAME);
        if (ver.exists())
            ver.delete();
        try {
            ver.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(ver))) {
            for (int i = 0; i < prevVersions.size(); i++) {
                if (prevVersions.size() <= i) {
                    out.writeInt(1);
                } else {
                    out.writeInt(prevVersions.get(i));
                }
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}