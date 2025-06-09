package com.example.dripz.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit foursquareRetrofit;
    private static Retrofit geocodingRetrofit;

    public static FoursquareApi getFoursquareApi() {
        if (foursquareRetrofit == null) {
            foursquareRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api.foursquare.com/v3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return foursquareRetrofit.create(FoursquareApi.class);
    }

    public static GeocodingApi getGeocodingApi() {
        if (geocodingRetrofit == null) {
            geocodingRetrofit = new Retrofit.Builder()
                    .baseUrl("https://nominatim.openstreetmap.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return geocodingRetrofit.create(GeocodingApi.class);
    }
}