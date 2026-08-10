package com.example.lightshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.databinding.FragmentCategoryBinding;
import com.example.lightshop.models.CategoryModel;
import com.example.lightshop.models.SubCategoryModel;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private SidebarAdapter sidebarAdapter;
    private CategoryAdapter subCategoryAdapter;
    private List<CategoryModel> categories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchCategories();
    }

    private void fetchCategories() {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchCategories(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    setupSidebar();
                    if (!categories.isEmpty()) {
                        fetchSubCategories(categories.get(0).getId()); // Fetch subcategories for first category
                    }
                } else {
                    Toast.makeText(getContext(), "Categories Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSidebar() {
        sidebarAdapter = new SidebarAdapter(categories, position -> {
            fetchSubCategories(categories.get(position).getId());
        });

        binding.rvSidebar.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSidebar.setAdapter(sidebarAdapter);
    }

    private void fetchSubCategories(long categoryId) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchSubCategories(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + categoryId,
                "*"
        ).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<SubCategoryModel>> call, @NonNull Response<List<SubCategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupSubCategories(response.body());
                } else {
                    Toast.makeText(getContext(), "SubCategories Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to fetch subcategories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSubCategories(List<SubCategoryModel> subCategories) {
        if (subCategoryAdapter == null) {
            subCategoryAdapter = new CategoryAdapter(subCategories);
            binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvCategories.setAdapter(subCategoryAdapter);
        } else {
            subCategoryAdapter.updateData(subCategories);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
