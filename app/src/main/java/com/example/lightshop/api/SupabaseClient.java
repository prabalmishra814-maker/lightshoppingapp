package com.example.lightshop.api;

import com.example.lightshop.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {
    // IMPORTANT: Secrets are now loaded from BuildConfig (local.properties)
    public static final String SUPABASE_URL = BuildConfig.SUPABASE_URL;
    public static final String SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY;

    private static SupabaseAuthService authService;
    private static SupabaseApiService apiService;

    public static SupabaseAuthService getAuthService() {
        if (authService == null) {
            authService = getRetrofit().create(SupabaseAuthService.class);
        }
        return authService;
    }

    public static SupabaseApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(SupabaseApiService.class);
        }
        return apiService;
    }

    private static Retrofit getRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(SUPABASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
