package com.example.baksomanagement.ui.checkout

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.OrderItem

class CheckoutAdapter(
    private val list: List<OrderItem>,
    private val onEditClick: (Int) -> Unit
) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgMenu: ImageView = view.findViewById(R.id.imgMenu)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvAddon: TextView = view.findViewById(R.id.tvAddon)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val btnEdit: Button = view.findViewById(R.id.btnEditOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false)

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

        if (item.addons.isEmpty()) {

            holder.tvAddon.text = "No add-on"

        } else {

            val addonNames =
                item.addons.joinToString(", ") { it.name }

            holder.tvAddon.text = addonNames
        }

        val total =
            (item.harga + item.addons.sumOf { it.price }) * item.quantity

        holder.tvTotal.text = "Rp $total"

        holder.btnEdit.setOnClickListener {

            Log.e(
                "CheckoutAdapter",
                "Edit clicked position: $position"
            )

            onEditClick(position)
        }
    }
}