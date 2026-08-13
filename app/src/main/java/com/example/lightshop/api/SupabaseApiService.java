package com.example.lightshop.api;

import com.example.lightshop.models.ProductModel;
import com.example.lightshop.models.CategoryModel;
import com.example.lightshop.models.WishlistModel;

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
import retrofit2.http.QueryMap;

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
        @Query("select") String select
    );

    @GET("rest/v1/{table}")
    Call<List<Map<String, Object>>> fetchDataWithFilters(
        @Path("table") String tableName,
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @QueryMap Map<String, String> filters
    );

    // Specific Fetch for Products returning ProductModel list
    @GET("rest/v1/Product")
    Call<List<ProductModel>> fetchProducts(
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Query("select") String select
    );

    @GET("rest/v1/Product")
    Call<List<ProductModel>> fetchProductsWithFilter(
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Query("select") String select,
        @QueryMap Map<String, String> filters
    );

    @GET("rest/v1/Product")
    Call<List<ProductModel>> searchProducts(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Query("select") String select,
            @Query("product_name") String ilikeFilter // e.g. "ilike.*query*"
    );

    @GET("rest/v1/products")
    Call<List<ProductModel>> fetchProductsPlural(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Query("select") String select
    );

    // Specific Fetch for Categories
    @GET("rest/v1/Category")
    Call<List<CategoryModel>> fetchCategories(
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Query("select") String select
    );

    @GET("rest/v1/categories")
    Call<List<CategoryModel>> fetchCategoriesPlural(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Query("select") String select
    );

    // Specific Fetch for SubCategories
    @GET("rest/v1/SubCategory")
    Call<List<com.example.lightshop.models.SubCategoryModel>> fetchSubCategories(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Query("category_id") String categoryIdFilter, // e.g., "eq.1"
            @Query("select") String select
    );

    @GET("rest/v1/subcategories")
    Call<List<com.example.lightshop.models.SubCategoryModel>> fetchSubCategoriesPlural(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Query("category_id") String categoryIdFilter,
            @Query("select") String select
    );

    // Specific Fetch for Wishlist - Fixed endpoint
    @GET("rest/v1/wishlist")
    Call<List<WishlistModel>> fetchWishlist(
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @Query("user_id") String userIdFilter, // e.g., "eq.uuid"
        @Query("select") String select // e.g., "*"
    );

    // Specific Fetch for Cart
    @GET("rest/v1/cart")
    Call<List<Map<String, Object>>> fetchCart(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Query("user_id") String userIdFilter,
            @Query("select") String select
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

    @PATCH("rest/v1/{table}")
    Call<ResponseBody> updateDataByColumn(
        @Path("table") String tableName,
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @QueryMap Map<String, String> filters,
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

    @DELETE("rest/v1/{table}")
    Call<ResponseBody> deleteDataByFilters(
        @Path("table") String tableName,
        @Header("apikey") String apiKey,
        @Header("Authorization") String authHeader,
        @QueryMap Map<String, String> filters
    );
}
