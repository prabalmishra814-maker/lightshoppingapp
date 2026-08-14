package com.amstudio.lightbasket.models;

import com.google.gson.annotations.SerializedName;

public class AuthModels {

    public static class SignUpRequest {
        public String email;
        public String password;
        public Data data;

        public SignUpRequest(String email, String password, String fullName) {
            this.email = email;
            this.password = password;
            this.data = new Data(fullName);
        }

        public static class Data {
            @SerializedName("full_name")
            public String fullName;

            public Data(String fullName) {
                this.fullName = fullName;
            }
        }
    }

    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class IdTokenRequest {
        public String provider = "google";
        @SerializedName("id_token")
        public String idToken;

        public IdTokenRequest(String idToken) {
            this.idToken = idToken;
        }
    }

    public static class AuthResponse {
        @SerializedName("access_token")
        public String accessToken;
        @SerializedName("refresh_token")
        public String refreshToken;
        @SerializedName("token_type")
        public String tokenType;
        @SerializedName("user")
        public User user;
        @SerializedName("error")
        public String error;
        @SerializedName("error_description")
        public String errorDescription;
    }

    public static class User {
        public String id;
        public String email;
        @SerializedName("user_metadata")
        public UserMetadata userMetadata;
    }

    public static class UserMetadata {
        @SerializedName("full_name")
        public String fullName;
        @SerializedName("avatar_url")
        public String avatarUrl;
    }
}

