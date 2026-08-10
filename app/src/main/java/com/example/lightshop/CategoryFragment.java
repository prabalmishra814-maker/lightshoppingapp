package com.example.lightshop;

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
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.api.SessionManager;
import com.example.lightshop.databinding.FragmentCategoryBinding;
import com.example.lightshop.models.CategoryModel;
import com.example.lightshop.models.SubCategoryModel;
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
    private Map<Integer, List<SubCategoryModel>> subCategoryMap = new HashMap<>();
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
        loadProfessionalData();
    }

    private void loadProfessionalData() {
        categories.clear();
        subCategoryMap.clear();

        // 1. Electronics
        addCategory(1, "Electronics", R.drawable.ic_electronics, new String[]{
                "Laptops", "Mobiles", "Tablets", "Headphones", "Smart Watches", "Speakers", "Cameras", "Televisions"
        });

        // 2. Mobiles
        addCategory(2, "Mobiles", R.drawable.ic_electronics, new String[]{
                "Smartphones", "Feature Phones", "Cases & Covers", "Screen Protectors", "Power Banks", "Chargers"
        });

        // 3. Men
        addCategory(3, "Men", R.drawable.ic_men, new String[]{
                "T-Shirts", "Shirts", "Jeans", "Trousers", "Shoes", "Watches", "Wallets", "Jackets"
        });

        // 4. Women
        addCategory(4, "Women", R.drawable.ic_women, new String[]{
                "Sarees", "Kurtis", "Dresses", "Tops", "Jeans", "Footwear", "Jewellery", "Handbags"
        });

        // 5. Kids
        addCategory(5, "Kids", R.drawable.ic_kids, new String[]{
                "Boys Clothing", "Girls Clothing", "Toys", "Baby Care", "Kids Shoes", "School Bags"
        });

        // 6. Beauty
        addCategory(6, "Beauty", R.drawable.ic_beauty, new String[]{
                "Makeup", "Skincare", "Hair Care", "Fragrances", "Personal Care"
        });

        // 7. Home & Kitchen
        addCategory(7, "Home & Kitchen", R.drawable.ic_home_cat, new String[]{
                "Kitchenware", "Cookware", "Home Decor", "Furniture", "Storage", "Cleaning"
        });

        // 8. Footwear
        addCategory(8, "Footwear", R.drawable.ic_shoes, new String[]{
                "Men Shoes", "Women Shoes", "Kids Shoes", "Sandals", "Slippers"
        });

        // 9. Grocery
        addCategory(9, "Grocery", R.drawable.ic_grocery, new String[]{
                "Staples", "Snacks", "Beverages", "Dairy", "Personal Care", "Household"
        });

        // 10. Accessories
        addCategory(10, "Accessories", R.drawable.ic_backpack, new String[]{
                "Bags", "Belts", "Sunglasses", "Caps", "Ties"
        });

        // 11. Watches
        addCategory(11, "Watches", R.drawable.ic_watch, new String[]{
                "Analog", "Digital", "Smart", "Luxury", "Sports"
        });

        // 12. Sports
        addCategory(12, "Sports", R.drawable.ic_sports, new String[]{
                "Cricket", "Football", "Badminton", "Fitness", "Outdoor"
        });

        // 13. Books
        addCategory(13, "Books", R.drawable.ic_book, new String[]{
                "Fiction", "Non-Fiction", "Self-Help", "Educational", "Comics"
        });

        // 14. Toys
        addCategory(14, "Toys", R.drawable.ic_kids, new String[]{
                "Action Figures", "Puzzles", "Educational Toys", "Dolls", "Remote Control"
        });

        setupSidebar();
        if (!categories.isEmpty()) {
            updateSectionLabel(categories.get(0).getCategoryName());
            setupSubCategories(subCategoryMap.get(categories.get(0).getId()));
        }
    }

    private void addCategory(int id, String name, int iconRes, String[] subCats) {
        CategoryModel cat = new CategoryModel();
        cat.setId(id);
        cat.setCategoryName(name);
        cat.setCategoryImage("res:" + iconRes);
        categories.add(cat);

        List<SubCategoryModel> subList = new ArrayList<>();
        for (int i = 0; i < subCats.length; i++) {
            SubCategoryModel sub = new SubCategoryModel();
            sub.setId(id * 100 + i);
            sub.setCategoryId(id);
            sub.setSubCategoryName(subCats[i]);
            subList.add(sub);
        }
        subCategoryMap.put(id, subList);
    }

    private void setupSidebar() {
        sidebarAdapter = new SidebarAdapter(categories, position -> {
            CategoryModel selected = categories.get(position);
            updateSectionLabel(selected.getCategoryName());
            setupSubCategories(subCategoryMap.get(selected.getId()));
        });

        binding.rvSidebar.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSidebar.setAdapter(sidebarAdapter);
    }

    private void updateSectionLabel(String categoryName) {
        if (binding != null) {
            binding.tvSectionLabel.setText("ALL " + categoryName);
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
