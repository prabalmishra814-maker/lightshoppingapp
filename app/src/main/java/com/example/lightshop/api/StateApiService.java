package com.example.lightshop.api;

import com.example.lightshop.models.StateResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface StateApiService {
    @GET("api/v2/admin/location/states")
    Call<StateResponse> getStates();
}
