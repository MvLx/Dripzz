package com.example.dripz.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;
import com.example.dripz.model.GeocodingResponse;

public interface GeocodingApi {
    @GET("search")
    Call<List<GeocodingResponse>> searchLocation(
            @Query("q") String query,
            @Query("format") String format,
            @Query("limit") int limit
    );
}