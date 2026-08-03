package com.example.lightshop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lightshop.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var sidebarAdapter: SidebarAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSidebar()
        setupCategories()
        setupBottomNav()
    }

    private fun setupSidebar() {
        val sidebarItems = listOf(
            Category("All Categories", iconRes = R.drawable.ic_all_categories),
            Category("Men", iconRes = R.drawable.ic_men),
            Category("Women", iconRes = R.drawable.ic_women),
            Category("Kids", iconRes = R.drawable.ic_kids),
            Category("Electronics", iconRes = R.drawable.ic_headphones),
            Category("Home", iconRes = R.drawable.ic_home),
            Category("Beauty", iconRes = R.drawable.ic_beauty),
            Category("Sports", iconRes = R.drawable.ic_sports),
            Category("Automotive", iconRes = R.drawable.ic_car),
            Category("Books", iconRes = R.drawable.ic_book),
            Category("Grocery", iconRes = R.drawable.ic_grocery)
        )

        sidebarAdapter = SidebarAdapter(sidebarItems) { position ->
            // Update main categories based on sidebar selection
            // For now, we'll just show different counts or filter if needed
        }

        binding.rvSidebar.layoutManager = LinearLayoutManager(this)
        binding.rvSidebar.adapter = sidebarAdapter
    }

    private fun setupCategories() {
        val categoryItems = listOf(
            Category("Men", "25,342 items", R.drawable.ic_men),
            Category("Women", "32,142 items", R.drawable.ic_women),
            Category("Kids", "12,532 items", R.drawable.ic_kids),
            Category("Electronics", "18,231 items", R.drawable.ic_headphones),
            Category("Home & Kitchen", "15,312 items", R.drawable.ic_home),
            Category("Beauty", "9,213 items", R.drawable.ic_beauty),
            Category("Sports", "7,432 items", R.drawable.ic_sports),
            Category("Automotive", "5,421 items", R.drawable.ic_car),
            Category("Books", "8,932 items", R.drawable.ic_book),
            Category("Grocery", "6,102 items", R.drawable.ic_grocery)
        )

        categoryAdapter = CategoryAdapter(categoryItems)
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = categoryAdapter
    }

    private fun setupBottomNav() {
        binding.bottomNavigation.selectedItemId = R.id.nav_category
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(android.content.Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_category -> true
                R.id.nav_orders -> {
                    startActivity(android.content.Intent(this, MyOrdersActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> { /* Navigate to Profile */ true }
                else -> false
            }
        }
    }
}
