package com.example.dripz.network;

import com.example.dripz.model.PhotoResponse;
import com.example.dripz.model.PlaceDetailResponse;
import com.example.dripz.model.PlacesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FoursquareApi {
    @GET("places/search")
    Call<PlacesResponse> searchPlaces(
            @Header("Authorization") String apiKey,
            @Query("ll") String latlon,
            @Query("categories") String categories,
            @Query("limit") int limit
    );
    @GET("places/{fsq_id}/photos")
    Call<java.util.List<PhotoResponse>> getPlacePhotos(
            @Header("Authorization") String apiKey,
            @Path("fsq_id") String fsqId,
            @Query("limit") int limit
    );
    @GET("places/{fsq_id}")
    Call<PlaceDetailResponse> getPlaceDetail(
            @Header("Authorization") String apiKey,
            @Path("fsq_id") String fsqId
    );
}