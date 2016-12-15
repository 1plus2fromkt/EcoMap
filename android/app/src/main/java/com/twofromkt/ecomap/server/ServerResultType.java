package com.twofromkt.ecomap.server;

public class ServerResultType {
    boolean resultSuccess;

    ServerResultType(boolean resultSuccess) {
        this.resultSuccess = resultSuccess;
    }

    public boolean resultSuccess() {
        return resultSuccess;
    }
}
