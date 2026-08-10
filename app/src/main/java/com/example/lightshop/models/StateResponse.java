package com.example.lightshop.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StateResponse {
    @SerializedName("states")
    public List<State> states;

    public static class State {
        @SerializedName("state_name")
        public String name;

        @Override
        public String toString() {
            return name;
        }
    }
}
