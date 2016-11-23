package server;


import db.DataUpdator9000;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Ser {

    private static class Sender implements Runnable{
        private static final int NEW_VERSION = 1, UP_TO_DATE = 0, END_OF_INPUT = -1, WRONG_FORMAT = -2,
                TOO_LARGE_VERSION = -3;
        Socket s;
        String dbName;
        int num;
        Sender (Socket s, int num) {
            this.s = s;
            this.num = num;
            dbName = num + ".db";
        }

        @Override
        public void run() {
            try {
                this.s.setTcpNoDelay(true);
                BufferedReader inVersion = new BufferedReader(new FileReader(DataUpdator9000.versionFileName));
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                ArrayList<Integer> phoneVersions = new ArrayList<>(), currVersions = new ArrayList<>();
                System.out.println("New client");
                try {
                    int v = in.readInt();
                    while (v != END_OF_INPUT) {
                        phoneVersions.add(v);
                        v = in.readInt();
                    }
                } catch (NumberFormatException e) {
                    out.write(WRONG_FORMAT);
                    s.close();
                    return;
                }
                System.out.println("Received versions");
                String temp;
                while ((temp = inVersion.readLine()) != null) {
                    currVersions.add(Integer.parseInt(temp));
                }
                if (phoneVersions.size() > currVersions.size()) {
                    out.writeInt(WRONG_FORMAT);
                }
                for (int i = 0; i < currVersions.size(); i++) {
                    if (i >= phoneVersions.size()) {
                        out.writeInt(NEW_VERSION);
                        out.flush();
                        sendFile(DataUpdator9000.dbFileName(i), out);
                        continue;
                    }
                    int curr = currVersions.get(i), ph = phoneVersions.get(i);
                    if (curr < ph) {
                        out.writeInt(TOO_LARGE_VERSION);
                    } else if (curr == ph) {
                        out.writeInt(UP_TO_DATE);
                    } else {
                        new File(dbName).delete();
                        DataUpdator9000.mergeChanges(DriverManager.getConnection("jdbc:sqlite:" + dbName),
                                i, ph, curr);
                        out.writeInt(NEW_VERSION);
                        sendFile(dbName, out);
                    }
                }
                s.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

        private void sendFile(String fileName, DataOutputStream out) throws IOException {
            File f = new File(fileName);
            byte[] arr = new byte[1024 * 8];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
            out.writeLong(f.length());
            out.flush();
            System.out.println(f.length());
            int curr = (int) f.length();
            while (curr > 0) {
                int sz = Math.min(arr.length, curr);
                in.read(arr, 0, sz);
                out.write(arr, 0, sz);
                out.flush();
                curr -= sz;
            }
            System.out.println(out.size());
            out.flush();
            System.out.println("sent file " + curr + " left");
        }

    }

    private static class Timer implements Runnable{

        @Override
        public void run() {
            while (true) {
                DataUpdator9000.main();
                try {
                    Thread.sleep((int) 1e3 * 43200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.sqlite.JDBC");
        ServerSocket s = new ServerSocket(4444);
        int id = 1;
        new Thread(new Timer()).run();
        while (true) {
            Socket socket = s.accept();
            new Thread(new Sender(socket, id++)).run();
        }
    }


}
