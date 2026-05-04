package com.example.baksomanagement.ui.menu

import android.util.Log
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
    private val onCheckedChange: (AddOn, Boolean) -> Unit
) : RecyclerView.Adapter<AddOnAdapterDetailMenu.ViewHolder>() {

    private val TAG = "AddOnAdapterDebug"

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cb: CheckBox = view.findViewById(R.id.cbAddon)
        val name: TextView = view.findViewById(R.id.tvAddonName)
        val price: TextView = view.findViewById(R.id.tvAddonPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_addon_detail_menu, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        val size = list.size
        Log.d(TAG, "getItemCount → $size")
        return size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addon = list[position]

        Log.d(TAG, "onBindViewHolder → position: $position")
        Log.d(TAG, "Addon → name: ${addon.name}, price: ${addon.price}")

        holder.name.text = addon.name
        holder.price.text = "Rp ${addon.price}"

        // Reset listener biar tidak ke-trigger saat recycle
        holder.cb.setOnCheckedChangeListener(null)

        holder.cb.isChecked = false
        Log.d(TAG, "Checkbox reset → unchecked")

        holder.cb.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "Checkbox changed → ${addon.name}, isChecked: $isChecked")

            try {
                onCheckedChange(addon, isChecked)
            } catch (e: Exception) {
                Log.e(TAG, "Error onCheckedChange: ${e.message}")
            }
        }
    }
}