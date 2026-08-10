package com.example.lightshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.api.SessionManager;
import com.example.lightshop.databinding.FragmentCategoryBinding;
import com.example.lightshop.models.CategoryModel;
import com.example.lightshop.models.SubCategoryModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private SidebarAdapter sidebarAdapter;
    private CategoryAdapter subCategoryAdapter;
    private List<CategoryModel> categories = new ArrayList<>();
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        setupHeaderActions();
        fetchCategories();
        updateCartBadge();
    }

    private void setupHeaderActions() {
        binding.btnSearch.setOnClickListener(v -> {
            // Future search implementation
        });

        binding.btnWishlist.setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), WishlistActivity.class));
        });

        binding.btnCart.setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), CartActivity.class));
        });
    }

    private void fetchCategories() {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchCategories(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    setupSidebar();
                    if (!categories.isEmpty()) {
                        updateSectionLabel(categories.get(0).getCategoryName());
                        fetchSubCategories(categories.get(0).getId());
                    }
                } else {
                    if (response.code() == 404) {
                        fetchCategoriesPlural();
                        return;
                    }
                    Toast.makeText(getContext(), "Categories Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable t) {
                android.util.Log.e("SupabaseError", "Categories Fetch Failed", t);
                fetchCategoriesPlural();
            }
        });
    }

    private void fetchCategoriesPlural() {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchCategoriesPlural(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    setupSidebar();
                    if (!categories.isEmpty()) {
                        updateSectionLabel(categories.get(0).getCategoryName());
                        fetchSubCategories(categories.get(0).getId());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable t) {
                android.util.Log.e("SupabaseError", "Categories Plural Fetch Failed", t);
            }
        });
    }

    private void setupSidebar() {
        sidebarAdapter = new SidebarAdapter(categories, position -> {
            CategoryModel selected = categories.get(position);
            updateSectionLabel(selected.getCategoryName());
            fetchSubCategories(selected.getId());
        });

        binding.rvSidebar.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSidebar.setAdapter(sidebarAdapter);
    }

    private void updateSectionLabel(String categoryName) {
        if (binding != null) {
            binding.tvSectionLabel.setText("All " + categoryName);
        }
    }

    private void fetchSubCategories(long categoryId) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchSubCategories(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + categoryId,
                "*"
        ).enqueue(new Callback<List<SubCategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<SubCategoryModel>> call, @NonNull Response<List<SubCategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupSubCategories(response.body());
                } else {
                    if (response.code() == 404) {
                        fetchSubCategoriesPlural(categoryId);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SubCategoryModel>> call, @NonNull Throwable t) {
                fetchSubCategoriesPlural(categoryId);
            }
        });
    }

    private void fetchSubCategoriesPlural(long categoryId) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchSubCategoriesPlural(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + categoryId,
                "*"
        ).enqueue(new Callback<List<SubCategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<SubCategoryModel>> call, @NonNull Response<List<SubCategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupSubCategories(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SubCategoryModel>> call, @NonNull Throwable t) {}
        });
    }

    private void setupSubCategories(List<SubCategoryModel> subCategories) {
        if (subCategoryAdapter == null) {
            subCategoryAdapter = new CategoryAdapter(subCategories);
            binding.rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
            binding.rvCategories.setAdapter(subCategoryAdapter);
        } else {
            subCategoryAdapter.updateData(subCategories);
        }
    }

    private void updateCartBadge() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            binding.tvCartBadge.setVisibility(View.GONE);
            return;
        }

        String authHeader = "Bearer " + sessionManager.getToken();
        SupabaseClient.getApiService().fetchCart(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + userId,
                "product_id"
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
                    if (count > 0) {
                        binding.tvCartBadge.setText(String.valueOf(count));
                        binding.tvCartBadge.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvCartBadge.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
