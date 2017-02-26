package com.ecomap.server.server;

import com.ecomap.server.db.DataUpdater;
import com.ecomap.server.util.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Ser {
    public static final String FOLDER_FOR_CLIENTS = "clients/";

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Class.forName("org.sqlite.JDBC");
        Logger.log("Server started");
        ServerSocket s = new ServerSocket(4444);
        long id = 1;
        new Thread(new Timer()).start();
        while (true) {
            Socket socket = s.accept();
            new Thread(new Sender(socket, id++)).start();
            Logger.log("New client, id = " + (id - 1));
        }
    }

    private static class Timer implements Runnable {

        @Override
        public void run() {
            boolean update = true;
            int cnt = 0;
            while (true) {
                if (update || cnt > 60 * 60 * 2) {
                    DataUpdater.update();
                    cnt = 0;
                }
                try {
//                    sleep((int) 1e3 * 43200);
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                update = readConfig();
                cnt++;
            }
        }

        private boolean readConfig() {
            try {
                String filename = "config/update.conf";
                Scanner in = new Scanner(new File(filename));
                int val = in.nextInt();
                if (val == 1) {
                    PrintWriter out = new PrintWriter(new File(filename));
                    out.println(0);
                    out.close();
                }
                return val == 1;
            } catch (IOException e) {
                Logger.err("Can not read timer config file");
                return false;
            }
        }
    }

}
