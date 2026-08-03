package com.example.lightshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupCategories()
        setupTopDeals()
        setupBottomNavigation()
    }

    private fun setupCategories() {
        val rvCategories = findViewById<RecyclerView>(R.id.rv_categories)
        val categories = listOf(
            Category("Men", R.drawable.ic_men, R.color.cat_men_bg),
            Category("Women", R.drawable.ic_women, R.color.cat_women_bg),
            Category("Electronics", R.drawable.ic_electronics, R.color.cat_electronics_bg),
            Category("Home", R.drawable.ic_home_cat, R.color.cat_home_bg),
            Category("Beauty", R.drawable.ic_beauty, R.color.cat_beauty_bg)
        )
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = CategoryAdapter(categories)
    }

    private fun setupTopDeals() {
        val rvDeals = findViewById<RecyclerView>(R.id.rv_deals)
        val products = listOf(
            Product("Analog Watch", "₹599", "₹999", "-40%", R.drawable.ic_watch),
            Product("Sports Shoes", "₹1,299", "₹1,999", "-35%", R.drawable.ic_shoes),
            Product("Backpack", "₹749", "₹999", "-25%", R.drawable.ic_backpack)
        )
        rvDeals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvDeals.adapter = ProductAdapter(products)
    }

    private fun setupBottomNavigation() {
        // Implementation for navigation item selection can be added here
    }

    // --- Data Models ---
    data class Category(val name: String, val iconRes: Int, val bgRes: Int)
    data class Product(val name: String, val price: String, val oldPrice: String, val discount: String, val imageRes: Int)

    // --- Adapters ---
    inner class CategoryAdapter(private val items: List<Category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.cat_name)
            val icon: ImageView = view.findViewById(R.id.cat_icon)
            val bg: View = view.findViewById(R.id.cat_bg)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = item.name
            holder.icon.setImageResource(item.iconRes)
            holder.bg.background.setTint(holder.itemView.context.getColor(item.bgRes))
        }

        override fun getItemCount() = items.size
    }

    inner class ProductAdapter(private val items: List<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tv_product_name)
            val price: TextView = view.findViewById(R.id.tv_price)
            val oldPrice: TextView = view.findViewById(R.id.tv_old_price)
            val discount: TextView = view.findViewById(R.id.tv_discount)
            val image: ImageView = view.findViewById(R.id.iv_product)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = item.name
            holder.price.text = item.price
            holder.oldPrice.text = item.oldPrice
            holder.discount.text = item.discount
            holder.image.setImageResource(item.imageRes)
        }

        override fun getItemCount() = items.size
    }
}