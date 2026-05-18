package com.example.baksomanagement.ui.orderStatus

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.OrderItem

class OrderStatusAdapter(
    private val list: List<OrderItem>
) : RecyclerView.Adapter<OrderStatusAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgMenu: ImageView = view.findViewById(R.id.imgMenu)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvAddon: TextView = view.findViewById(R.id.tvAddon)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false) // reuse layout
        Log.e("CheckoutAdapter", "onCreateViewHolder dipanggil")
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.imgMenu.context)
            .load(item.imageUrl)
            .into(holder.imgMenu)

        holder.tvNama.text = item.nama
        holder.tvQty.text = "Jumlah: ${item.quantity}"
        Log.e("CheckoutAdapter", "onBindViewHolder dipanggil dengan position: $position")
        Log.e("CheckoutAdapter", "Nama: ${item.nama}")
        Log.e("CheckoutAdapter", "Jumlah: ${item.quantity}")
        holder.tvAddon.text =
            if (item.addons.isEmpty()) "No add-on"
            else item.addons.joinToString(", ") { it.name }
    }
}