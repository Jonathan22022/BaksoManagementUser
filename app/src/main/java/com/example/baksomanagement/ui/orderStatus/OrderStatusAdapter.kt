package com.example.baksomanagement.ui.orderStatus

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

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val imgMenu: ImageView =
            view.findViewById(R.id.imgMenu)

        val tvNama: TextView =
            view.findViewById(R.id.tvNama)

        val tvAddon: TextView =
            view.findViewById(R.id.tvAddon)

        val tvQty: TextView =
            view.findViewById(R.id.tvQty)

        val tvTotal: TextView =
            view.findViewById(R.id.tvTotal)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_status_orderan,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.tvNama.text = item.nama

        holder.tvQty.text =
            "Qty : ${item.quantity}"

        val addonText =
            if (item.addons.isEmpty()) {
                "Tidak ada addon"
            } else {
                item.addons.joinToString(", ") {
                    it.name
                }
            }

        holder.tvAddon.text = addonText

        val addonPrice =
            item.addons.sumOf { it.price }

        val total =
            (item.harga + addonPrice) *
                    item.quantity

        holder.tvTotal.text =
            "Rp $total"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.imgMenu)
    }
}