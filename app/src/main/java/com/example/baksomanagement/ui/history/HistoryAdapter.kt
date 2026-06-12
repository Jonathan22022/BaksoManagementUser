package com.example.baksomanagement.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.History
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val list: MutableList<History>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view){

        val imgMenu =
            view.findViewById<ImageView>(
                R.id.imgMenu
            )

        val tvNama =
            view.findViewById<TextView>(
                R.id.tvNama
            )

        val tvDate =
            view.findViewById<TextView>(
                R.id.tvDate
            )

        val tvQty =
            view.findViewById<TextView>(
                R.id.tvQty
            )

        val tvStatus =
            view.findViewById<TextView>(
                R.id.tvStatus
            )

        val tvTotal =
            view.findViewById<TextView>(
                R.id.tvTotal
            )

        val btnDetail =
            view.findViewById<Button>(
                R.id.btnDetail
            )

        val cbSelect =
            view.findViewById<CheckBox>(
                R.id.cbSelect
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_history,
                parent,
                false
            )
        )
    }

    override fun getItemCount() =
        list.size

    fun getSelectedItems(): List<History>{
        return list.filter { it.selected }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item =
            list[position]

        holder.tvNama.text =
            item.nama

        holder.tvQty.text =
            "Jumlah : ${item.quantity}"

        holder.tvTotal.text =
            "Rp ${item.total}"

        holder.tvStatus.text =
            if(item.status == "selesai")
                "Completed"
            else
                "Cancelled"

        holder.tvDate.text =
            SimpleDateFormat(
                "dd MMM yyyy HH:mm",
                Locale("id")
            ).format(
                Date(item.createdAt)
            )

        Glide.with(holder.itemView)
            .load(item.imageUrl)
            .into(holder.imgMenu)

        holder.cbSelect.setOnCheckedChangeListener(null)

        holder.cbSelect.isChecked =
            item.selected

        holder.cbSelect.setOnCheckedChangeListener { _, isChecked ->

            item.selected = isChecked

            onSelectionChanged()
        }

        holder.btnDetail.setOnClickListener {

            val bundle =
                Bundle().apply {

                    putString(
                        "ORDER_ID",
                        item.orderId
                    )
                }

            it.findNavController()
                .navigate(
                    R.id.action_historyFragment_to_detailHistoryOrderanUserFragment,
                    bundle
                )
        }
    }
}