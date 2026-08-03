package com.example.lightshop.api;

import com.example.lightshop.models.AuthModels;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SupabaseAuthService {

    @POST("auth/v1/signup")
    Call<AuthModels.AuthResponse> signUp(
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Body AuthModels.SignUpRequest request
    );

    @POST("auth/v1/token?grant_type=password")
    Call<AuthModels.AuthResponse> login(
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Body AuthModels.LoginRequest request
    );

    @POST("auth/v1/token?grant_type=id_token")
    Call<AuthModels.AuthResponse> loginWithIdToken(
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Body AuthModels.IdTokenRequest request
    );
}
