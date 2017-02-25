package com.ecomap.server;

import com.ecomap.server.util.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    final static int a = 0;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 4444);
        socket.setTcpNoDelay(true);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.writeUTF("test string");
        out.flush();
        out.writeUTF("test string 2");
        out.close();
//        int tests = in.readInt();
//        for (int test = 0; test < tests; test++) {
//            System.out.println("Reading " + test);
//            read("receive" + test + ".txt", in);
//            System.out.println("Done reading " + test);
//        }
//        out.println(2);
//        out.println(0);
//        out.println("End of version");
//        System.out.println("Sending");
//        for (int i = 0; i < 2; i++) {
//            File f = new File("test" + i + ".db");

    }

//    static File read(String filename, DataInputStream in) throws IOException {
//        File f = new File(filename);
//        FileOutputStream out = new FileOutputStream(f);
//        byte[] buffer = new byte[1024];
//        long size = in.readLong();
//        while (size != 0) {
//            int dataRead = in.read(buffer);
//            out.write(buffer, 0, dataRead);
//            size -= dataRead;
//        }
//        out.close();
//
//        return f;
//    }

    private static ArrayList<Integer> getVersions() {
        int[] v = {1, 1, 1};
        ArrayList<Integer> res = new ArrayList<>();
        for (int i : v) {
            res.add(i);
        }
        return res;
    }

    private static String getDiffPath() {
        return "testdiff/";
    }

    private static Pair<ArrayList<Boolean>, ArrayList<Integer>> download()
            throws IOException {
        Logger.log("starting download");
        Socket socket = new Socket("localhost", 4444);
        socket.setTcpNoDelay(true);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ArrayList<Integer> versions = getVersions();
        for (int i : versions) {
            out.writeInt(i);
        }
        out.writeInt(-1);
        out.flush();
        int i = 0, version;
        new File(getDiffPath()).mkdir();
        ArrayList<Boolean> ans = new ArrayList<>();
        ArrayList<Integer> currVers = new ArrayList<>();
        while ((version = in.readInt()) != -1) {
            File f = new File(getDiffPath() + "diff" + i + ".db");
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
            } else if (version == 0) {
                currVers.add(versions.get(i));
                ans.add(false);
            } else if (version < 0) {
                currVers.add(versions.get(i));
                ans.add(false);
                System.err.println("ALERT! Error answer " + version);
            }
            i++;
        }
        Logger.log("finished download");
        return new Pair<>(ans, currVers);
    }
}