package com.twofromkt.ecomap.server;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.JsonWriter;
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
import java.io.StringWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.ArrayList;

class Downloader {
    public static ArrayList<Pair<Double, Double>> data;

    private static final String VERSION_FILE_NAME = "version.txt";
    private static final String SERVER_IP = "37.46.133.69";

    private static final int CAT_NUM = 2; // TODO REMOVE THIS PLEASE THIS IS VERY BAD

    private static final String TAG = "DOWNLOADER";

    private static final int NEW_VERSION = 1, UP_TO_DATE = 0, END_OF_INPUT = -1, WRONG_FORMAT = -2,
            TOO_LARGE_VERSION = -3;

    static ServerResultType update(Context context) {
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

    private static Pair<ArrayList<Boolean>, ArrayList<Integer>> download(Context context)
            throws IOException {
        Log.d(TAG, "Connecting to server");
        Socket socket = new Socket(SERVER_IP, 4444);
        socket.setTcpNoDelay(true);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        sendClientInfo(out, context);

        int i = 0;
        int result;
        new File(context.getFilesDir(), DBAdapter.getDiffPath()).mkdir();
        ArrayList<Boolean> ans = new ArrayList<>();
        ArrayList<Integer> currVers = new ArrayList<>();
        ArrayList<Integer> versions = getVersions(context);
        while ((result = in.readInt()) != -1) {
            File diffFile = new File(context.getFilesDir(), DBAdapter.getDiffPath() + "diff" + i + ".db");
            diffFile.createNewFile();
            Log.d(TAG, "Getting database " + i + " from server");
            if (result == NEW_VERSION) {
                currVers.add(in.readInt());
                readFile(in, diffFile);
                ans.add(true);
            } else if (result == UP_TO_DATE) {
                currVers.add(versions.get(i));
                ans.add(false);
            } else if (result < 0) {
                currVers.add(versions.get(i));
                ans.add(false);
                System.err.println("ALERT! Error answer " + result);
            }
            i++;
        }
        Log.d(TAG, "Finished download");
        return new Pair<>(ans, currVers);
    }

    private static boolean readFile(DataInputStream in, File dest) {
        try (FileOutputStream fileOut = new FileOutputStream(dest)) {
            long fileSize = in.readLong();
            byte[] buffer = new byte[1024 * 8];
            int cnt;
            while (fileSize > 0
                    && (cnt = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                fileOut.write(buffer, 0, cnt);
                fileSize -= cnt;
            }
            fileOut.close();
            Log.d(TAG, dest.getName() + ": " + dest.length() + " bytes");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void sendClientInfo(DataOutputStream out, Context context) throws IOException {
        String clientInfo = getClientInfoJson(context);
        out.writeUTF(clientInfo);
        out.flush();
    }

    /**
     * Get json string representing this client.
     * Contains info about database versions and client app version.
     * NOTE: when changing this consider changing server side too!
     *
     * @param context Current context
     * @return Json string with info about client
     * @throws IOException
     */
    private static String getClientInfoJson(Context context) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name("versions");
        writer.beginArray();

        ArrayList<Integer> versions = getVersions(context);
        for (int i : versions) {
            writer.value(i);
        }

        writer.endArray();
        writer.name("requestedDatabases");
        writer.beginArray();
        writer.value("RECYCLE"); // CHANGE THIS WHEN OTHER DB REQUESTED
        writer.value("ECOMOBILE");
        writer.endArray();
        writer.name("appVersion");
        writer.value(0);
        writer.endObject();
        return sw.toString();
    }

    /**
     * Check if version file exists, create write 0s if not
     *
     * @param context
     */
    private static void initVersionFile(Context context) {
        File ver = new File(context.getFilesDir(), VERSION_FILE_NAME);
        if (!ver.exists()) {
            try {
                ver.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(ver))) {
            for (int i = 0; i < CAT_NUM; i++) {
                out.writeInt(0);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Integer> getVersions(Context context) throws IOException {
        File ver = new File(context.getFilesDir(), VERSION_FILE_NAME);
        if (!ver.exists()) {
            initVersionFile(context);
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

    private static void updateVersionFile(ArrayList<Integer> prevVersions, Context context) {
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