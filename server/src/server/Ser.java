package server;


import db.DataUpdater;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Ser {
    private static final String folderForClients = "clients/";

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Class.forName("org.sqlite.JDBC");
        System.out.println("Server start");
        ServerSocket s = new ServerSocket(4444);
        int id = 1;
        new Thread(new Timer()).start();
        while (true) {
            Socket socket = s.accept();
            new Thread(new Sender(socket, id++)).start();
            System.out.println("New client " + id);
        }
    }

    private static class Sender implements Runnable {
        private static final int NEW_VERSION = 1, UP_TO_DATE = 0, END_OF_INPUT = -1, WRONG_FORMAT = -2,
                TOO_LARGE_VERSION = -3;
        Socket s;
        String dbName;
        int num;

        Sender(Socket s, int num) {
            this.s = s;
            this.num = num;
            dbName = num + ".db";
        }

        @Override
        public void run() {
            try(DataInputStream in = new DataInputStream(s.getInputStream());
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    BufferedReader inVersion = new BufferedReader(new FileReader(DataUpdater.versionFileName))) {
                ArrayList<Integer> phoneVersions = new ArrayList<>(), currVersions = new ArrayList<>();
                try {
                    int v = in.readInt();
                    while (v != END_OF_INPUT) {
                        phoneVersions.add(v);
                        v = in.readInt();
                    }
                    System.out.println("Version file received");
                } catch (NumberFormatException e) {
                    out.write(WRONG_FORMAT);
                    s.close();
                    return;
                }
                String temp;
                while ((temp = inVersion.readLine()) != null) {
                    currVersions.add(Integer.parseInt(temp));
                }
                if (phoneVersions.size() > currVersions.size()) {
                    out.writeInt(WRONG_FORMAT);
                }
                System.out.println("Starting to send databases");
                for (int i = 0; i < currVersions.size(); i++) {
                    if (i >= phoneVersions.size()) {
                        System.out.println("First version of " + i + " database sent");
                        out.writeInt(NEW_VERSION);
                        out.writeInt(currVersions.get(i));
                        out.flush();
                        sendFile(DataUpdater.dbFileName(i), out);
                        continue;
                    }
                    int curr = currVersions.get(i), ph = phoneVersions.get(i);
                    if (curr < ph) {
                        out.writeInt(TOO_LARGE_VERSION);
                    } else if (curr == ph) {
                        System.out.println("Up-to-date");
                        out.writeInt(UP_TO_DATE);
                    } else {
                        Files.deleteIfExists(new File(folderForClients, dbName).toPath());
                        System.out.println("New version of database");
                        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + folderForClients + dbName)) {
                            DataUpdater.mergeChanges(c,
                                    i, ph, curr);
                            out.writeInt(NEW_VERSION);
                            out.writeInt(currVersions.get(i));
                            sendFile(dbName, out);
                        }
                    }
                    System.out.println("Sent " + i + " database");
                }
                out.writeInt(END_OF_INPUT);
                s.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

        private void sendFile(String fileName, DataOutputStream out) throws IOException {
            File f = new File(folderForClients, fileName);
            byte[] arr = new byte[1024 * 8];
            Files.createFile(f.toPath());
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
            out.writeLong(f.length());
            out.flush();
            int curr = (int) f.length();
            while (curr > 0) {
                int sz = Math.min(arr.length, curr);
                in.read(arr, 0, sz);
                out.write(arr, 0, sz);
                out.flush();
                curr -= sz;
            }
            out.flush();
        }

    }

    private static class Timer implements Runnable {

        @Override
        public void run() {
            while (true) {
                DataUpdater.updateData();
                try {
                    sleep((int) 1e3 * 43200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
