package com.example.dripz.model;

import java.util.List;
import java.util.ArrayList;

public class Place {
    public String fsq_id;
    public String name;
    public Location location;
    public List<Category> categories = new ArrayList<>();
    public Geocodes geocodes;
    public Hours hours;
    public List<Photo> photos = new ArrayList<>();
    public String offlineImagePath; // For offline image storage

    public static class Location {
        public String address;
        public String country;
    }

    public static class Category {
        public String name;
    }

    public static class Hours {
        public String display;
    }

    public static class Photo {
        public String prefix;
        public String suffix;
        public int width;
        public int height;
        public String getUrl() {
            String p = prefix != null ? prefix : "";
            String s = suffix != null ? suffix : "";
            if (!p.endsWith("/")) p += "/";
            if (!s.startsWith("/")) s = "/" + s;
            return p + "original" + s;
        }
    }
}