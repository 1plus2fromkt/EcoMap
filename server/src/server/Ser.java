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
                BufferedReader inSocket = new BufferedReader(new InputStreamReader(s.getInputStream()));
                BufferedReader inVersion = new BufferedReader(new FileReader(DataUpdator9000.versionFileName));
                DataOutputStream outputStream = new DataOutputStream(s.getOutputStream());
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                ArrayList<Integer> phoneVersions = new ArrayList<>(), currVersions = new ArrayList<>();
                System.out.println("New client");
                try {
                    String s = inSocket.readLine();
                    while (!s.contains("End of v")) {
                        phoneVersions.add(Integer.parseInt(s));
                        s = inSocket.readLine();
                    }
                } catch (NumberFormatException e) {
                    out.println("Go to hell");
                    return;
                }
                System.out.println("Received versions");
                String temp;
                while ((temp = inVersion.readLine()) != null) {
                    currVersions.add(Integer.parseInt(temp));
                }
                if (phoneVersions.size() > currVersions.size()) {
                    out.println("Wrong number of categories");
                }
                for (int i = 0; i < currVersions.size(); i++) {
                    if (i >= phoneVersions.size()) {
                        out.println("new version");
                        sendFile(DataUpdator9000.dbFileName(i), out, outputStream);
                        continue;
                    }
                    int curr = currVersions.get(i), ph = phoneVersions.get(i);
                    if (curr < ph) {
                        out.println(createErrorString(i));
                    } else if (curr == ph) {
                        out.println("up-to-date");
                    } else {
                        new File(dbName).delete();
                        DataUpdator9000.mergeChanges(DriverManager.getConnection("jdbc:sqlite:" + dbName),
                                i, ph, curr);
                        out.println("new version");
                        sendFile(dbName, out, outputStream);
                    }
                }
                s.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

        private void sendFile(String fileName, PrintWriter pw, DataOutputStream out) throws IOException {
            File f = new File(fileName);
            byte[] arr = new byte[1024];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
            pw.println(f.length());
            System.out.println(f.length());
            int curr = (int)f.length();
            while (curr > 0) {
                in.read(arr, 0, arr.length);
                out.write(arr);
                curr -= arr.length;
            }
            System.out.println("sent file " + curr + " left");
        }

        private static String createErrorString(int i) {
            return "Bad request " + i + " category";
        }
    }

    private static class Timer implements Runnable{

        @Override
        public void run() {
            while (true) {
                DataUpdator9000.main();
                try {
                    Thread.sleep((int) 1e3*10);
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
//        new Thread(new Timer()).run();
        while (true) {
            Socket socket = s.accept();
            new Thread(new Sender(socket, id++)).run();
        }
    }


}
