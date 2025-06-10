package com.example.dripz.model;

import java.util.List;

public class PlaceDetailResponse {
    public String fsq_id;
    public String name;
    public Location location;
    public List<Category> categories;
    public String description; // kadang null
    public List<Photo> photos;
    public String website;
    public Hours hours;

    public static class Location {
        public String address;
        public String country;
        public String region;
        public String locality;
        public String postcode;
        public String formatted_address;
    }

    public static class Category {
        public String id;
        public String name;
    }

    public static class Photo {
        public String id;
        public String prefix;
        public String suffix;
        public int width;
        public int height;
    }

    public static class Hours {
        public List<Open> open;
        public boolean is_open;

        public static class Open {
            public String rendered_time;
            public String day;
        }
    }
}