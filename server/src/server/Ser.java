package server;


import db.DataUpdator9000;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Ser {

    static class Sender implements Runnable{
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
                BufferedReader inSocket = new BufferedReader(new InputStreamReader(s.getInputStream()));
                BufferedReader inVersion = new BufferedReader(new FileReader(DataUpdator9000.versionFileName));
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                ArrayList<Integer> phoneVersions = new ArrayList<>(), currVersions = new ArrayList<>();
                try {
                    readVersions(inSocket, phoneVersions);
                } catch (NumberFormatException e) {
                    out.write("Go to hell");
                    return;
                }
                readVersions(inVersion, currVersions);
                if (phoneVersions.size() > currVersions.size()) {
                    out.write("Wrong number of categories");
                }
                for (int i = 0; i < currVersions.size(); i++) {
                    if (i >= phoneVersions.size()) {
                        sendFile(DataUpdator9000.dbFileName(i));
                        continue;
                    }
                    int curr = currVersions.get(i), ph = phoneVersions.get(i);
                    if (curr < ph) {
                        out.write(createErrorString(i));
                    } else if (curr == ph) {
                        out.write("up-to-date");
                    } else {
                        new File(dbName).delete();
                        DataUpdator9000.mergeChanges(DriverManager.getConnection("jdbc:sqlite:" + dbName),
                                i, ph, curr);
                        out.write("new version");
                        sendFile(dbName);
                    }
                    inSocket.close();
                    out.close();
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

        private void sendFile(String fileName) throws IOException {
            File f = new File(fileName);
            byte[] arr = new byte[(int) f.length()];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
            in.read(arr, 0, arr.length);
            OutputStream out = s.getOutputStream();
            out.write(arr, 0, arr.length);
            out.flush();
            s.close();
        }

        private static void readVersions(BufferedReader in, ArrayList<Integer> arr) throws IOException {
            String s;
            while ((s = in.readLine()) != null) {
                arr.add(Integer.parseInt(s));
            }
        }

        private static String createErrorString(int i) {
            return "Bad request " + i + " category";
        }
    }
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.sqlite.JDBC");
        ServerSocket s = new ServerSocket(4444);
        int id = 1;
        while (true) {
            Socket socket = s.accept();
            new Thread(new Sender(socket, id++));
        }
    }


}
