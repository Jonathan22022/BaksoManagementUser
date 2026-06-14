package com.example.baksomanagement.ui.menu

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Menu

class MenuAdapter(
    private val menuList: List<Menu>,
    private val onItemClick: (Menu) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private val TAG = "MenuAdapterDebug"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgMenu)
        val name: TextView = itemView.findViewById(R.id.tvMenuName)
        val desc: TextView = itemView.findViewById(R.id.tvMenuDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        val size = menuList.size
        Log.d(TAG, "getItemCount: $size")
        return size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menuList[position]

        Log.d(TAG, "onBindViewHolder → position: $position, menu: ${menu.namaMenu}")

        holder.name.text = menu.namaMenu
        holder.desc.text = menu.description

        // Debug URL gambar
        if (menu.gambarUrl.isEmpty()) {
            Log.e(TAG, "Image URL EMPTY for menu: ${menu.namaMenu}")
        } else {
            Log.d(TAG, "Loading image: ${menu.gambarUrl}")
        }

        Glide.with(holder.itemView.context)
            .load(menu.gambarUrl)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            Log.d(TAG, "Item clicked → ${menu.namaMenu} (ID: ${menu.id})")
            onItemClick(menu)
        }
    }
}