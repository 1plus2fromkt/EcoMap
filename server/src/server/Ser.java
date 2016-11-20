package server;

import db.DBMover;

import java.sql.Connection;

public class Ser {
    public static void main(String[] args) {
        DBMover.mergeChanges(null, 0, 0, 1);
    }


}
