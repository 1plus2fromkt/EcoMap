package com.ecomap.server.server;

import com.ecomap.server.db.DataUpdater;
import com.ecomap.server.util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
            while (true) {
                DataUpdater.update();
                try {
                    sleep((int) 1e3 * 43200);
//                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
