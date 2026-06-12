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
    private val onEditClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "CheckoutAdapterDebug"
    }

    init {
        Log.d(TAG, "================================")
        Log.d(TAG, "CheckoutAdapter dibuat")
        Log.d(TAG, "Jumlah item = ${list.size}")
        Log.d(TAG, "================================")
    }

    inner class ViewHolder(view: View) :
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

        val btnEdit: Button =
            view.findViewById(R.id.btnEditOrder)

        val btnDelete: ImageView =
            view.findViewById(
                R.id.btnDeleteOrder
            )

        init {
            Log.d(
                TAG,
                "ViewHolder dibuat -> adapterPosition = $adapterPosition"
            )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        Log.d(
            TAG,
            "onCreateViewHolder() dipanggil"
        )

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_checkout,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        Log.d(
            TAG,
            "getItemCount() = ${list.size}"
        )

        return list.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        Log.d(
            TAG,
            "================================"
        )

        Log.d(
            TAG,
            "onBindViewHolder posisi = $position"
        )

        val item = list[position]

        Log.d(
            TAG,
            """
            DATA ITEM
            ------------------------
            Position  : $position
            Menu ID   : ${item.menu_id}
            Nama      : ${item.nama}
            Harga     : ${item.harga}
            Quantity  : ${item.quantity}
            Image URL : ${item.imageUrl}
            Addon Qty : ${item.addons.size}
            ------------------------
            """.trimIndent()
        )

        Glide.with(holder.imgMenu.context)
            .load(item.imageUrl)
            .into(holder.imgMenu)

        Log.d(
            TAG,
            "Load gambar sukses -> ${item.imageUrl}"
        )

        holder.tvNama.text = item.nama

        holder.tvQty.text =
            "Jumlah: ${item.quantity}"

        if (item.addons.isEmpty()) {

            Log.d(
                TAG,
                "Item tidak memiliki addon"
            )

            holder.tvAddon.text = "No add-on"

        } else {

            val addonNames =
                item.addons.joinToString(", ") {
                    it.name
                }

            val addonPrice =
                item.addons.sumOf {
                    it.price
                }

            Log.d(
                TAG,
                """
                ADDON
                Nama Addon  = $addonNames
                Total Addon = $addonPrice
                """.trimIndent()
            )

            holder.tvAddon.text = addonNames
        }

        val addonTotal =
            item.addons.sumOf {
                it.price
            }

        val total =
            (
                    item.harga +
                            addonTotal
                    ) * item.quantity

        Log.d(
            TAG,
            """
            PERHITUNGAN TOTAL
            Harga Menu  = ${item.harga}
            Addon Total = $addonTotal
            Qty         = ${item.quantity}
            Grand Item  = $total
            """.trimIndent()
        )

        holder.tvTotal.text = "Rp $total"

        holder.btnDelete.setOnClickListener {

            val pos = holder.adapterPosition

            if (pos != RecyclerView.NO_POSITION) {
                onDeleteClick(pos)
            }
        }

        holder.btnEdit.setOnClickListener {

            val adapterPosition =
                holder.adapterPosition

            Log.d(
                TAG,
                "================================"
            )

            Log.d(
                TAG,
                "Tombol Edit diklik"
            )

            Log.d(
                TAG,
                "bindingAdapterPosition = $adapterPosition"
            )

            Log.d(
                TAG,
                """
                ITEM YANG DIEDIT
                Menu ID  = ${item.menu_id}
                Nama     = ${item.nama}
                Qty      = ${item.quantity}
                Total    = $total
                """.trimIndent()
            )

            if (
                adapterPosition != RecyclerView.NO_POSITION
            ) {

                Log.d(
                    TAG,
                    "Posisi valid, menjalankan callback"
                )

                onEditClick(adapterPosition)

            } else {

                Log.e(
                    TAG,
                    "Posisi adapter tidak valid!"
                )
            }
        }

        Log.d(
            TAG,
            "Binding selesai untuk posisi = $position"
        )

        Log.d(
            TAG,
            "================================"
        )
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)

        Log.d(
            TAG,
            "View direcycle -> position = ${holder.adapterPosition}"
        )
    }

    override fun onAttachedToRecyclerView(
        recyclerView: RecyclerView
    ) {
        super.onAttachedToRecyclerView(recyclerView)

        Log.d(
            TAG,
            "Adapter attached ke RecyclerView"
        )
    }

    override fun onDetachedFromRecyclerView(
        recyclerView: RecyclerView
    ) {
        super.onDetachedFromRecyclerView(recyclerView)

        Log.d(
            TAG,
            "Adapter detached dari RecyclerView"
        )
    }
}