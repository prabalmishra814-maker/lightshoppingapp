package com.example.lightshop; // This tells Java which folder this file belongs to. Example: app.folder.name

import android.util.Log; // This lets us print messages to the console for debugging. Example: printing "Hello!"

import com.example.lightshop.api.SupabaseApiService; // Import the rules for how to talk to Supabase.
import com.example.lightshop.api.SupabaseClient; // Import the helper that connects us to Supabase.

import java.util.HashMap; // A tool to store data in pairs, like "Name: John".
import java.util.List; // A tool to store a list of items, like [1, 2, 3].
import java.util.Map; // A general tool for storing pairs of data.

import okhttp3.ResponseBody; // A way to get the raw response from the internet.
import retrofit2.Call; // A request sent to the internet.
import retrofit2.Callback; // A plan for what to do when the internet replies.
import retrofit2.Response; // The actual answer we get back from the internet.

/**
 * This class shows how to Fetch, Add, Update, and Delete data from any Supabase table.
 * You can copy these methods into your Activities or ViewModels.
 */
public class SupabaseCrudExample { // This is the main container for our Supabase code.
    private static final String TAG = "SupabaseCRUD"; // A nickname for this class to find logs easily. Example: "DEBUG_TAG"
    private static final String TABLE_NAME = "your_table_name"; // The name of the table in Supabase. Example: "users" or "products"
    private static final String AUTH_HEADER = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY; // The security key to prove we are allowed to access data.

    private final SupabaseApiService apiService; // The engine that sends the actual web requests.

    public SupabaseCrudExample() { // The constructor that sets everything up.
        this.apiService = SupabaseClient.getApiService(); // Connect the engine to our Supabase client.
    }

    // 1. FETCH DATA (Read) - Getting information from the database.
    public void fetchAllData() { // Method to get everything from the table.
        apiService.fetchData(TABLE_NAME, SupabaseClient.SUPABASE_ANON_KEY, AUTH_HEADER, "*") // Ask Supabase for all (*) columns.
                .enqueue(new Callback<List<Map<String, Object>>>() { // Put the request in a queue and wait for the answer.
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) { // This runs if the internet responds.
                if (response.isSuccessful() && response.body() != null) { // Check if we got a "Success" message and the data isn't empty.
                    List<Map<String, Object>> data = response.body(); // Save the list of items we received.
                    Log.d(TAG, "Fetched " + data.size() + " items"); // Print how many items we found. Example: "Fetched 5 items"
                    for (Map<String, Object> item : data) { // Go through each item one by one.
                        Log.d(TAG, "Item: " + item.toString()); // Print the details of the item. Example: "Item: {id=1, name=Laptop}"
                    }
                } else { // This runs if Supabase said "No" or something went wrong.
                    Log.e(TAG, "Fetch failed: " + response.code()); // Print the error code. Example: "404"
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) { // This runs if there's a connection error (like no WiFi).
                Log.e(TAG, "Error: " + t.getMessage()); // Print the specific error message. Example: "No internet"
            }
        });
    }

    // 2. ADD DATA (Create) - Saving new information to the database.
    public void insertData(String name, String description) { // Method to add a new item with a name and description.
        Map<String, Object> newData = new HashMap<>(); // Create a container for the new information.
        newData.put("name", name); // Add the name to the container. Example: "name" -> "Milk"
        newData.put("description", description); // Add the description. Example: "description" -> "Buy 2 liters"

        apiService.addData(TABLE_NAME, SupabaseClient.SUPABASE_ANON_KEY, AUTH_HEADER, "return=representation", newData) // Send the new data to Supabase.
                .enqueue(new Callback<List<Map<String, Object>>>() { // Wait for Supabase to tell us if it worked.
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) { // Runs when Supabase answers.
                if (response.isSuccessful()) { // Check if the save was successful.
                    Log.d(TAG, "Data inserted successfully!"); // Print a success message.
                } else { // Runs if the save failed.
                    Log.e(TAG, "Insert failed: " + response.code()); // Print why it failed.
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) { // Runs if the network failed.
                Log.e(TAG, "Error: " + t.getMessage()); // Print the error.
            }
        });
    }

    // 3. UPDATE DATA (Update) - Changing existing information.
    public void updateData(int id, String newName) { // Method to change the name of an item using its ID.
        Map<String, Object> updateMap = new HashMap<>(); // Create a container for the changes.
        updateMap.put("name", newName); // Add the new name. Example: "name" -> "Apples"

        String idFilter = "eq." + id; // A way to say "find the item where ID is equal to this number". Example: "eq.5"

        apiService.updateData(TABLE_NAME, SupabaseClient.SUPABASE_ANON_KEY, AUTH_HEADER, idFilter, updateMap) // Send the update request.
                .enqueue(new Callback<ResponseBody>() { // Wait for the result.
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) { // Runs when finished.
                if (response.isSuccessful()) { // Check if the change worked.
                    Log.d(TAG, "Update successful!"); // Print success.
                } else { // Runs if it failed.
                    Log.e(TAG, "Update failed: " + response.code()); // Print error code.
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { // Runs if network failed.
                Log.e(TAG, "Error: " + t.getMessage()); // Print error.
            }
        });
    }

    // 4. DELETE DATA (Delete) - Removing information forever.
    public void deleteData(int id) { // Method to remove an item using its ID.
        String idFilter = "eq." + id; // Say "find the item where ID is equal to this". Example: "eq.10"

        apiService.deleteData(TABLE_NAME, SupabaseClient.SUPABASE_ANON_KEY, AUTH_HEADER, idFilter) // Send delete request to Supabase.
                .enqueue(new Callback<ResponseBody>() { // Wait for the result.
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) { // Runs when finished.
                if (response.isSuccessful()) { // Check if the item was deleted.
                    Log.d(TAG, "Delete successful!"); // Print success.
                } else { // Runs if it failed.
                    Log.e(TAG, "Delete failed: " + response.code()); // Print error code.
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { // Runs if network failed.
                Log.e(TAG, "Error: " + t.getMessage()); // Print error.
            }
        });
    }
}
