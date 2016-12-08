package places.Recycle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class RecycleHandler {
    private static final int MAX_ID = 100, TRESHOLD = 600;

    public static void updateData(Connection c) {
        int emptyPoints = 0;
        for (int i = 1; i < MAX_ID; i++) {
            try {
                if ((new RecyclePlace(i)).writeToDB(c)) {
                    emptyPoints = 0;
                } else {
                    emptyPoints++;
                }
            } catch (SQLException e) {
                System.out.println("Something was wrong with database");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Couldn't connect to recycle");
                e.printStackTrace();
            }
            if (emptyPoints > TRESHOLD) {
                break;
            }
        }

    }

}
