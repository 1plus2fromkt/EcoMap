package com.ecomap.server.server;

import com.ecomap.server.DataModel;
import com.ecomap.server.Source;
import com.ecomap.server.db.DBUtil;
import com.ecomap.server.db.DataUpdater;
import com.ecomap.server.util.Logger;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.ecomap.server.server.Ser.FOLDER_FOR_CLIENTS;

class Sender implements Runnable {
    private static final int NEW_VERSION = 1, UP_TO_DATE = 0, END_OF_INPUT = -1,
            WRONG_FORMAT = -2, TOO_LARGE_VERSION = -3;
    private Socket socket;
    private String currDbName;
    private long id;
    private List<Integer> currVersions;

    Sender(Socket socket, long id) {
        this.socket = socket;
        this.id = id;
        currDbName = id + ".db";
    }

    @Override
    public void run() {
        try (
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            ClientInfo info = readClientInfo(in);
            readVersions();
            Logger.log("Sending databases to client " + id);
            for (String s : info.requestedDatabases) {
                switch (s) {
                    case "RECYCLE":
                        updateClientData(Source.RECYCLE, info, out);
                        continue;
                    case "ECOMOBILE":
                        updateClientData(Source.ECOMOBILE, info, out);
                        continue;
                    default:
                        Logger.err("Error: unknown source type " + s + "requested");
                }
            }
            out.writeInt(END_OF_INPUT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private void readVersions() {
        currVersions = new ArrayList<>();
        try (Scanner in = new Scanner(DataUpdater.getVersionFile())) {
            while (in.hasNextInt()) {
                currVersions.add(in.nextInt());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateClientData(Source source, ClientInfo info, DataOutputStream out) throws IOException {
        DataModel model = DataModel.read(source);
        int currVer = currVersions.get(model.id);
        int phoneVer = info.getVersion(model.id);
        if (phoneVer == 0) {
            Logger.log("First version of " + source + " database sent to client " + id);
            out.writeInt(NEW_VERSION);
            out.writeInt(currVersions.get(model.id));
            out.flush();
            sendFile(model.folderName + '/' + model.dbName, out);
            return;
        }
        if (currVer == phoneVer) {
            Logger.log("Up-to-date client " + id);
            out.writeInt(UP_TO_DATE);
        } else {
            Files.deleteIfExists(new File(FOLDER_FOR_CLIENTS, currDbName).toPath());
            Logger.log("New version (" + currVer + ") of database sent");
            DBUtil.mergeChanges(FOLDER_FOR_CLIENTS + currDbName, model, phoneVer);
            out.writeInt(NEW_VERSION);
            out.writeInt(currVersions.get(model.id));
            sendFile(FOLDER_FOR_CLIENTS + currDbName, out);
        }
        Logger.log("Sent " + source + " database to client " + id);
    }

    private ClientInfo readClientInfo(DataInputStream inputStream) throws IOException {
        try {
            String json = inputStream.readUTF();
            System.out.println(json);
            Gson g = new Gson();
            return g.fromJson(json, ClientInfo.class);
        } catch (IOException e) {
            throw new IOException("Can not read client info");
        }
    }

    private void sendFile(String fileName, DataOutputStream out) throws IOException {
        File f = new File(fileName);
        byte[] arr = new byte[1024 * 8];
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

    private static class ClientInfo {
        List<Integer> versions;
        List<String> requestedDatabases;
        int appVersion;

        int getVersion(int index) {
            return versions.get(index);
        }
    }

}