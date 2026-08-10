package com.example.lightshop.api;

import com.example.lightshop.models.LocationIQResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationIQApiService {
    @GET("v1/reverse.php")
    Call<LocationIQResponse> reverseGeocode(
        @Query("key") String apiKey,
        @Query("lat") double lat,
        @Query("lon") double lon,
        @Query("format") String format
    );
}
