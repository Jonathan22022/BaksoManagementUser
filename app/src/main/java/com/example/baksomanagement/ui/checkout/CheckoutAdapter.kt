package com.example.baksomanagement.ui.checkout

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.OrderItem

class CheckoutAdapter(
    private val list: List<OrderItem>
) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvAddon: TextView = view.findViewById(R.id.tvAddon)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false)
        Log.e("CheckoutAdapter", "onCreateViewHolder dipanggil")
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("CheckoutAdapter", "onBindViewHolder dipanggil dengan position: $position")
        val item = list[position]
        holder.tvNama.text = item.nama
        holder.tvQty.text = "Jumlah: ${item.quantity}"
        Log.e("CheckoutAdapter", "Nama: ${item.nama}")
        Log.e("CheckoutAdapter", "Jumlah: ${item.quantity}")
        // tampilkan addon
        if (item.addons.isEmpty()) {
            holder.tvAddon.text = "No add-on"
        } else {
            val addonNames = item.addons.joinToString(", ") { it.name }
            holder.tvAddon.text = addonNames
        }
        val total = (item.harga + item.addons.sumOf { it.price }) * item.quantity
        holder.tvTotal.text = "Rp $total"
        Log.e("CheckoutAdapter", "Total: $total")
    }
}