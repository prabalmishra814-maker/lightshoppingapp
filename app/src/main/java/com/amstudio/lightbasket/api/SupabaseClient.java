package com.amstudio.lightbasket.api;

import android.content.Context;

import androidx.annotation.Nullable;

import com.amstudio.lightbasket.BuildConfig;
import com.amstudio.lightbasket.models.AuthModels;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {
    public static final String SUPABASE_URL = BuildConfig.SUPABASE_URL;
    public static final String SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY;

    private static SupabaseAuthService authService;
    private static SupabaseApiService apiService;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

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
        if (com.amstudio.lightbasket.BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .authenticator(new Authenticator() {
                    @Nullable
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        if (appContext == null) return null;
                        
                        // Avoid infinite loops
                        if (responseCount(response) >= 2) {
                            return null;
                        }

                        SessionManager sessionManager = new SessionManager(appContext);
                        String refreshToken = sessionManager.getRefreshToken();

                        if (refreshToken == null || refreshToken.isEmpty()) {
                            return null;
                        }

                        // Synchronously refresh token
                        SupabaseAuthService refreshService = getRefreshRetrofit().create(SupabaseAuthService.class);
                        Map<String, String> body = new HashMap<>();
                        body.put("refresh_token", refreshToken);

                        retrofit2.Response<AuthModels.AuthResponse> refreshResponse = refreshService.refreshToken(
                                SUPABASE_ANON_KEY,
                                "Bearer " + SUPABASE_ANON_KEY,
                                body
                        ).execute();

                        if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                            AuthModels.AuthResponse auth = refreshResponse.body();
                            sessionManager.updateAccessToken(auth.accessToken, auth.refreshToken);

                            return response.request().newBuilder()
                                    .header("Authorization", "Bearer " + auth.accessToken)
                                    .build();
                        }

                        return null;
                    }
                })
                .build();

        String url = SUPABASE_URL;
        if (url != null && !url.endsWith("/")) {
            url += "/";
        }

        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private static Retrofit getRefreshRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (com.amstudio.lightbasket.BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        String url = SUPABASE_URL;
        if (url != null && !url.endsWith("/")) {
            url += "/";
        }

        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}

