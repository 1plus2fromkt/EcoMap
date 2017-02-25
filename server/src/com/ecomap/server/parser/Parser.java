package com.ecomap.server.parser;

import com.ecomap.server.Source;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static List<Place> loadPlaces(Source source) {
        switch (source) {
            case RECYCLE:
                return RecycleParser.loadPlaces();
            case ECOMOBILE:
                return EcomobileParser.loadPlaces();
        }
        return new ArrayList<>();
    }
}
