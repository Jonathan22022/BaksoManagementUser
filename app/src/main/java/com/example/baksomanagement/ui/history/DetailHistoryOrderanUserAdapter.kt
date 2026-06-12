package com.example.baksomanagement.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.OrderItem

class DetailHistoryOrderanUserAdapter(
    private val list: List<OrderItem>
) : RecyclerView.Adapter<
        DetailHistoryOrderanUserAdapter.ViewHolder>() {

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val imgMenu =
            view.findViewById<ImageView>(
                R.id.imgMenu
            )

        val tvMenu =
            view.findViewById<TextView>(
                R.id.tvMenu
            )

        val tvDesc =
            view.findViewById<TextView>(
                R.id.tvDesc
            )

        val tvAddon =
            view.findViewById<TextView>(
                R.id.tvAddon
            )

        val tvQty =
            view.findViewById<TextView>(
                R.id.tvQty
            )

        val tvHarga =
            view.findViewById<TextView>(
                R.id.tvHarga
            )

        val tvSubtotal =
            view.findViewById<TextView>(
                R.id.tvSubtotal
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_detail_history_order,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount() =
        list.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.tvMenu.text =
            item.nama

        holder.tvDesc.text =
            if (item.catatan.isBlank())
                "Catatan : -"
            else
                "Catatan : ${item.catatan}"

        holder.tvAddon.text =
            if (item.addons.isEmpty())
                "Addon : -"
            else
                "Addon : ${
                    item.addons.joinToString(", ") {
                        it.name
                    }
                }"

        holder.tvQty.text =
            "Jumlah : ${item.quantity}"

        holder.tvHarga.text =
            "Harga : Rp ${item.harga}"

        val addonTotal =
            item.addons.sumOf {
                it.price
            }

        val subtotal =
            (item.harga + addonTotal) *
                    item.quantity

        holder.tvSubtotal.text =
            "Subtotal : Rp $subtotal"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.bakso)
            .error(R.drawable.bakso)
            .into(holder.imgMenu)
    }
}