package com.example.lightshop;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.lightshop.api.LocationIQApiService;
import com.example.lightshop.api.SessionManager;
import com.example.lightshop.api.StateApiService;
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.models.LocationIQResponse;
import com.example.lightshop.models.StateResponse;
import com.example.lightshop.utils.StatusBarUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddAddressActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etPhone, etPincode, etCity, etHouseNo, etRoadName, etLandmark;
    private AutoCompleteTextView spinnerState;
    private RadioGroup rgAddressType;
    private Button btnSaveAddress, btnCurrentLocation;
    private LocationIQApiService locationIQApi;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String LOCATIONIQ_API_KEY = "pk.d8803d14af06c71661f4c0952f74bd17";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_add_address);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        setupToolbar();
        fetchStates();
        initLocationApi();
        fetchExistingAddress();

        btnSaveAddress.setOnClickListener(v -> validateAndSaveAddress());
        btnCurrentLocation.setOnClickListener(v -> checkLocationPermission());
    }

    private void initLocationApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us1.locationiq.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        locationIQApi = retrofit.create(LocationIQApiService.class);
    }

    private void initViews() {
        etFullName = findViewById(R.id.et_full_name);
        etPhone = findViewById(R.id.et_phone);
        etPincode = findViewById(R.id.et_pincode);
        spinnerState = findViewById(R.id.spinner_state);
        etCity = findViewById(R.id.et_city);
        etHouseNo = findViewById(R.id.et_house_no);
        etRoadName = findViewById(R.id.et_road_name);
        etLandmark = findViewById(R.id.et_landmark);
        rgAddressType = findViewById(R.id.rg_address_type);
        btnSaveAddress = findViewById(R.id.btn_save_address);
        btnCurrentLocation = findViewById(R.id.btn_current_location);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        btnCurrentLocation.setText("Locating...");
        btnCurrentLocation.setEnabled(false);

        // Flipkart-style Fast Location Fetch using FusedLocationProvider (Google Play Services)
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        fetchAddressFromCoords(location.getLatitude(), location.getLongitude());
                    } else {
                        // Fallback to Last Known if current fails
                        fusedLocationClient.getLastLocation().addOnSuccessListener(this, lastLoc -> {
                            if (lastLoc != null) {
                                fetchAddressFromCoords(lastLoc.getLatitude(), lastLoc.getLongitude());
                            } else {
                                resetLocationButton();
                                Toast.makeText(this, "Could not determine location", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    resetLocationButton();
                    Toast.makeText(this, "Location Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void resetLocationButton() {
        btnCurrentLocation.setText("Use Current Location");
        btnCurrentLocation.setEnabled(true);
    }

    private void fetchAddressFromCoords(double lat, double lon) {
        locationIQApi.reverseGeocode(LOCATIONIQ_API_KEY, lat, lon, "json").enqueue(new Callback<LocationIQResponse>() {
            @Override
            public void onResponse(@NonNull Call<LocationIQResponse> call, @NonNull Response<LocationIQResponse> response) {
                btnCurrentLocation.setText("Use Current Location");
                btnCurrentLocation.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LocationIQResponse.Address addr = response.body().address;
                    if (addr != null) {
                        etPincode.setText(addr.postcode);
                        etCity.setText(addr.city != null ? addr.city : addr.suburb);
                        spinnerState.setText(addr.state, false);
                        etRoadName.setText(addr.road);
                        etHouseNo.setText(addr.houseNumber);
                        Toast.makeText(AddAddressActivity.this, "Location detected!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationIQResponse> call, @NonNull Throwable t) {
                btnCurrentLocation.setText("Use Current Location");
                btnCurrentLocation.setEnabled(true);
                Toast.makeText(AddAddressActivity.this, "Failed to get address", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStates() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cdn-api.co-vin.in/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StateApiService api = retrofit.create(StateApiService.class);
        api.getStates().enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(@NonNull Call<StateResponse> call, @NonNull Response<StateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<StateResponse.State> states = response.body().states;
                    ArrayAdapter<StateResponse.State> adapter = new ArrayAdapter<>(
                            AddAddressActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            states
                    );
                    spinnerState.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<StateResponse> call, @NonNull Throwable t) {
                Toast.makeText(AddAddressActivity.this, "Failed to load states", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void validateAndSaveAddress() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phoneInput = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String phone = "+91 " + phoneInput;
        String pincode = etPincode.getText() != null ? etPincode.getText().toString().trim() : "";
        String state = spinnerState.getText().toString().trim();
        String city = etCity.getText() != null ? etCity.getText().toString().trim() : "";
        String houseNo = etHouseNo.getText() != null ? etHouseNo.getText().toString().trim() : "";
        String roadName = etRoadName.getText() != null ? etRoadName.getText().toString().trim() : "";
        String landmark = etLandmark.getText() != null ? etLandmark.getText().toString().trim() : "";

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(pincode)) {
            etPincode.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(state)) {
            spinnerState.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(city)) {
            etCity.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(houseNo)) {
            etHouseNo.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(roadName)) {
            etRoadName.setError("Required");
            return;
        }

        int selectedTypeId = rgAddressType.getCheckedRadioButtonId();
        RadioButton selectedType = findViewById(selectedTypeId);
        String addressType = selectedType.getText().toString();

        saveAddressToSupabase(fullName, phone, pincode, state, city, houseNo, roadName, landmark, addressType);
    }

    private void saveAddressToSupabase(String fullName, String phone, String pincode, String state, String city, String houseNo, String roadName, String landmark, String addressType) {
        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserId();
        
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Map for JSON address format
        Map<String, Object> addressJson = new java.util.HashMap<>();
        addressJson.put("name", fullName);
        addressJson.put("number", phone);
        addressJson.put("house_no", houseNo);
        addressJson.put("road_name", roadName);
        addressJson.put("city", city);
        addressJson.put("state", state);
        addressJson.put("pincode", pincode);
        addressJson.put("landmark", landmark);
        addressJson.put("type", addressType);

        Map<String, Object> updateData = new java.util.HashMap<>();
        updateData.put("uid", userId); // Critical for UPSERT to work
        updateData.put("address", addressJson);
        updateData.put("name", fullName);
        updateData.put("number", phone);
        
        btnSaveAddress.setEnabled(false);
        btnSaveAddress.setText("Saving...");

        String userToken = sessionManager.getToken();
        String authHeader = "Bearer " + (userToken != null ? userToken : SupabaseClient.SUPABASE_ANON_KEY);
        
        // Use addData with merge-duplicates for UPSERT
        SupabaseClient.getApiService().addData(
                "Users",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "resolution=merge-duplicates,return=representation",
                updateData
        ).enqueue(new Callback<java.util.List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<java.util.List<Map<String, Object>>> call, @NonNull Response<java.util.List<Map<String, Object>>> response) {
                btnSaveAddress.setEnabled(true);
                btnSaveAddress.setText("Save Address");

                if (response.isSuccessful()) {
                    Toast.makeText(AddAddressActivity.this, "Address Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddAddressActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    try {
                        if (response.errorBody() != null) {
                            android.util.Log.e("SupabaseUpdate", "Error Body: " + response.errorBody().string());
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }

            @Override
            public void onFailure(@NonNull Call<java.util.List<Map<String, Object>>> call, @NonNull Throwable t) {
                btnSaveAddress.setEnabled(true);
                btnSaveAddress.setText("Save Address");
                Toast.makeText(AddAddressActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchExistingAddress() {
        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserId();
        if (TextUtils.isEmpty(userId)) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        java.util.Map<String, String> filters = new java.util.HashMap<>();
        filters.put("uid", "eq." + userId);
        filters.put("select", "address");

        SupabaseClient.getApiService().fetchDataWithFilters(
                "Users",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<java.util.List<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<java.util.List<java.util.Map<String, Object>>> call, @NonNull Response<java.util.List<java.util.Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Object addressObj = response.body().get(0).get("address");
                    if (addressObj instanceof java.util.Map) {
                        java.util.Map<String, Object> address = (java.util.Map<String, Object>) addressObj;
                        populateFields(address);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<java.util.List<java.util.Map<String, Object>>> call, @NonNull Throwable t) {}
        });
    }

    private void populateFields(java.util.Map<String, Object> address) {
        try {
            if (address.containsKey("name")) etFullName.setText((String) address.get("name"));
            if (address.containsKey("number")) {
                String fullPhone = (String) address.get("number");
                if (fullPhone != null && fullPhone.startsWith("+91 ")) {
                    etPhone.setText(fullPhone.substring(4));
                } else {
                    etPhone.setText(fullPhone);
                }
            }
            if (address.containsKey("pincode")) etPincode.setText((String) address.get("pincode"));
            if (address.containsKey("city")) etCity.setText((String) address.get("city"));
            if (address.containsKey("state")) spinnerState.setText((String) address.get("state"), false);
            if (address.containsKey("house_no")) etHouseNo.setText((String) address.get("house_no"));
            if (address.containsKey("road_name")) etRoadName.setText((String) address.get("road_name"));
            if (address.containsKey("landmark")) etLandmark.setText((String) address.get("landmark"));
            
            if (address.containsKey("type")) {
                String type = (String) address.get("type");
                if ("Home".equalsIgnoreCase(type)) {
                    rgAddressType.check(R.id.rb_home);
                } else if ("Work".equalsIgnoreCase(type)) {
                    rgAddressType.check(R.id.rb_work);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
