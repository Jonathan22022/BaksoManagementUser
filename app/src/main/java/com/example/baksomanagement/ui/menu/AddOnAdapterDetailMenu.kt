package com.example.baksomanagement.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.AddOn

class AddOnAdapterDetailMenu(
    private val list: List<AddOn>,
    private val stockMap: Map<String, Boolean>,
    private val selectedAddons: List<AddOn>,
    private val onCheckedChange: (AddOn, Boolean) -> Unit
) : RecyclerView.Adapter<AddOnAdapterDetailMenu.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cb: CheckBox = view.findViewById(R.id.cbAddon)
        val name: TextView = view.findViewById(R.id.tvAddonName)
        val price: TextView = view.findViewById(R.id.tvAddonPrice)
        val stock: TextView = view.findViewById(R.id.tvAddonStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_addon_detail_menu, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val addon = list[position]

        holder.name.text = addon.name
        holder.price.text = "Rp ${addon.price}"

        val isAvailable = stockMap[addon.id] ?: true

        holder.cb.setOnCheckedChangeListener(null)

        holder.cb.isChecked =
            selectedAddons.any {
                it.id == addon.id
            }

        holder.cb.isEnabled = isAvailable

        if (isAvailable) {
            holder.stock.visibility = View.GONE
            holder.cb.alpha = 1f
        } else {
            holder.stock.visibility = View.VISIBLE
            holder.cb.alpha = 0.5f
        }

        holder.cb.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(addon, isChecked)
        }
    }
}