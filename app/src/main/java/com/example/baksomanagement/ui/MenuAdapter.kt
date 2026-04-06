package com.example.baksomanagement.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Menu

class MenuAdapter(
    private val menuList: List<Menu>,
    private val onItemClick: (Menu) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgMenu)
        val name: TextView = itemView.findViewById(R.id.tvMenuName)
        val desc: TextView = itemView.findViewById(R.id.tvMenuDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = menuList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menuList[position]
        holder.name.text = menu.name
        holder.desc.text = menu.description
        holder.image.setImageResource(menu.image)

        // CLICK CARD
        holder.itemView.setOnClickListener {
            onItemClick(menu)
        }
    }
}