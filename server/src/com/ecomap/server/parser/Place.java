package com.ecomap.server.parser;

import java.sql.Connection;

public abstract class Place {
    public abstract void writeToDB(Connection destConn);
}
