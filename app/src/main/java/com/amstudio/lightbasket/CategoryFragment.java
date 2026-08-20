package com.amstudio.lightbasket;

import android.content.Intent;
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
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.databinding.FragmentCategoryBinding;
import com.amstudio.lightbasket.models.CategoryModel;
import com.amstudio.lightbasket.models.SubCategoryModel;
import java.util.ArrayList;
import java.util.HashMap;
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
        
        setupRecyclerViews();
        setupSwipeRefresh();
        fetchCategories();
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::fetchCategories);
        binding.swipeRefresh.setColorSchemeResources(R.color.dark_navy);
    }

    private void setupRecyclerViews() {
        sidebarAdapter = new SidebarAdapter(categories, position -> {
            CategoryModel selected = categories.get(position);
            updateSectionLabel(selected.getCategoryName());
            fetchSubCategories(selected.getId());
        });
        binding.rvSidebar.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSidebar.setAdapter(sidebarAdapter);

        subCategoryAdapter = new CategoryAdapter(new ArrayList<>());
        binding.rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvCategories.setAdapter(subCategoryAdapter);
    }

    private void fetchCategories() {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchCategories(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                if (binding != null) binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    sidebarAdapter.notifyDataSetChanged();
                    
                    if (!categories.isEmpty()) {
                        updateSectionLabel(categories.get(0).getCategoryName());
                        fetchSubCategories(categories.get(0).getId());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                if (binding != null) binding.swipeRefresh.setRefreshing(false);
                if (getContext() != null) Toast.makeText(getContext(), "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSubCategories(int categoryId) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchSubCategories(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + categoryId,
                "*"
        ).enqueue(new Callback<List<SubCategoryModel>>() {
            @Override
            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    subCategoryAdapter.updateData(response.body());
                } else {
                    subCategoryAdapter.updateData(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                subCategoryAdapter.updateData(new ArrayList<>());
            }
        });
    }

    private void updateSectionLabel(String categoryName) {
        if (binding != null) {
            binding.tvSectionLabel.setText("ALL " + categoryName);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

