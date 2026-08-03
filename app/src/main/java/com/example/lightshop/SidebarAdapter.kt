package com.example.lightshop

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class SidebarAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SidebarAdapter.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: View = view.findViewById(R.id.sidebar_container)
        val icon: ImageView = view.findViewById(R.id.sidebar_icon)
        val name: TextView = view.findViewById(R.id.sidebar_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sidebar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.name.text = category.name
        holder.icon.setImageResource(category.iconRes)

        if (position == selectedPosition) {
            holder.container.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.selected_bg))
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary))
            holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.primary))
        } else {
            holder.container.setBackgroundColor(Color.TRANSPARENT)
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_subtitle))
            holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.text_hint))
        }

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            onItemClick(selectedPosition)
        }
    }

    override fun getItemCount() = categories.size
}
