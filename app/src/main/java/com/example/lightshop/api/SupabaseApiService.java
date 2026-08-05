package com.example.lightshop.api;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Generic Interface for Supabase PostgREST CRUD operations.
 * Replace 'items' with your actual table name in usage.
 */
public interface SupabaseApiService {

    // 1. FETCH (Select all or with filters)
    @GET("rest/v1/{table}")
    Call<List<Map<String, Object>>> fetchData(
        @Path("table") String tableName,
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Query("select") String select // e.g., "*"
    );

    // 2. ADD (Insert)
    @POST("rest/v1/{table}")
    Call<List<Map<String, Object>>> addData(
        @Path("table") String tableName,
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Header("Prefer") String prefer, // e.g., "return=representation"
        @Body Object data
    );

    // 3. UPDATE (Patch)
    @PATCH("rest/v1/{table}")
    Call<ResponseBody> updateData(
        @Path("table") String tableName,
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Query("id") String idFilter, // e.g., "eq.123"
        @Body Map<String, Object> data
    );

    // 4. DELETE
    @DELETE("rest/v1/{table}")
    Call<ResponseBody> deleteData(
        @Path("table") String tableName,
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Query("id") String idFilter // e.g., "eq.123"
    );
}
