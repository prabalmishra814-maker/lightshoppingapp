package com.example.lightshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.lightshop.databinding.FragmentCategoryBinding;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private SidebarAdapter sidebarAdapter;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSidebar();
        setupCategories();
    }

    private void setupSidebar() {
        List<Category> sidebarItems = new ArrayList<>();
        sidebarItems.add(new Category("All Categories", R.drawable.ic_all_categories));
        sidebarItems.add(new Category("Men", R.drawable.ic_men));
        sidebarItems.add(new Category("Women", R.drawable.ic_women));
        sidebarItems.add(new Category("Kids", R.drawable.ic_kids));
        sidebarItems.add(new Category("Electronics", R.drawable.ic_headphones));
        sidebarItems.add(new Category("Home", R.drawable.ic_home));
        sidebarItems.add(new Category("Beauty", R.drawable.ic_beauty));
        sidebarItems.add(new Category("Sports", R.drawable.ic_sports));
        sidebarItems.add(new Category("Automotive", R.drawable.ic_car));
        sidebarItems.add(new Category("Books", R.drawable.ic_book));
        sidebarItems.add(new Category("Grocery", R.drawable.ic_grocery));

        sidebarAdapter = new SidebarAdapter(sidebarItems, position -> {
            // Update main categories based on sidebar selection
        });

        binding.rvSidebar.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSidebar.setAdapter(sidebarAdapter);
    }

    private void setupCategories() {
        List<Category> categoryItems = new ArrayList<>();
        categoryItems.add(new Category("Men", "25,342 items", R.drawable.ic_men));
        categoryItems.add(new Category("Women", "32,142 items", R.drawable.ic_women));
        categoryItems.add(new Category("Kids", "12,532 items", R.drawable.ic_kids));
        categoryItems.add(new Category("Electronics", "18,231 items", R.drawable.ic_headphones));
        categoryItems.add(new Category("Home & Kitchen", "15,312 items", R.drawable.ic_home));
        categoryItems.add(new Category("Beauty", "9,213 items", R.drawable.ic_beauty));
        categoryItems.add(new Category("Sports", "7,432 items", R.drawable.ic_sports));
        categoryItems.add(new Category("Automotive", "5,421 items", R.drawable.ic_car));
        categoryItems.add(new Category("Books", "8,932 items", R.drawable.ic_book));
        categoryItems.add(new Category("Grocery", "6,102 items", R.drawable.ic_grocery));

        categoryAdapter = new CategoryAdapter(categoryItems);
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
