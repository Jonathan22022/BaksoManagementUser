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
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val status: String = "pending"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_status_orderan, parent, false) // reuse layout
        Log.e("StatusOrderanAdapter", "onCreateViewHolder dipanggil")
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        Log.e(
            "StatusOrderanAdapter",
            "getItemCount = ${list.size}"
        )

        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val total =
            (item.harga +
                    item.addons.sumOf { it.price }) *
                    item.quantity

        holder.tvTotal.text = "Rp $total"
        Glide.with(holder.imgMenu.context)
            .load(item.imageUrl)
            .into(holder.imgMenu)

        holder.tvNama.text = item.nama
        holder.tvQty.text = "Jumlah: ${item.quantity}"
        Log.e("StatusOrderanAdapter", "onBindViewHolder dipanggil dengan position: $position")
        Log.e("StatusOrderanAdapter", "Nama: ${item.nama}")
        Log.e("StatusOrderanAdapter", "Jumlah: ${item.quantity}")
        holder.tvAddon.text =
            if (item.addons.isEmpty()) "No add-on"
            else item.addons.joinToString(", ") { it.name }
    }
}