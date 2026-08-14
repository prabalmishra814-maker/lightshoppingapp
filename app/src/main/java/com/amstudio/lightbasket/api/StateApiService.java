package com.amstudio.lightbasket.api;

import com.amstudio.lightbasket.models.StateResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface StateApiService {
    @GET("api/v2/admin/location/states")
    Call<StateResponse> getStates();
}

