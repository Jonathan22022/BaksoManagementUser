package com.example.baksomanagement.ui.search

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

class SearchAdapter(
    private var menuList: List<Menu>,
    private val onItemClick: ((Menu) -> Unit)? = null
) : RecyclerView.Adapter<SearchAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imgMenu: ImageView = itemView.findViewById(R.id.imgMenu)
        val tvMenuName: TextView = itemView.findViewById(R.id.tvMenuName)
        val tvMenuDesc: TextView = itemView.findViewById(R.id.tvMenuDesc)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)

        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MenuViewHolder,
        position: Int
    ) {
        val menu = menuList[position]
        Log.d("ADAPTER", menu.namaMenu)
        holder.tvMenuName.text = menu.namaMenu
        holder.tvMenuDesc.text = menu.description

        Glide.with(holder.itemView.context)
            .load(menu.gambarUrl)
            .placeholder(R.drawable.bakso)
            .into(holder.imgMenu)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(menu)
        }
    }

    override fun getItemCount(): Int = menuList.size

    fun updateData(newList: List<Menu>) {
        menuList = newList
        notifyDataSetChanged()
    }
}