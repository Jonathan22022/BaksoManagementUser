package com.example.baksomanagement.ui.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Cart

class CartAdapter(private val cartList: List<Cart>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val img = itemView.findViewById<ImageView>(R.id.imgCart)
        val name = itemView.findViewById<TextView>(R.id.tvName)
        val desc = itemView.findViewById<TextView>(R.id.tvDesc)
        val addons = itemView.findViewById<TextView>(R.id.tvAddons)
        val extra = itemView.findViewById<TextView>(R.id.tvExtra)
        val total = itemView.findViewById<TextView>(R.id.tvTotal)
        val btnOrder = itemView.findViewById<Button>(R.id.btnOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        Log.e("CartAdapter", "onCreateViewHolder dipanggil")
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = cartList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val cart = cartList[position]

        holder.name.text = cart.name
        holder.desc.text = cart.desc
        holder.addons.text = cart.addons
        holder.extra.text = cart.extra
        holder.total.text = "Total : Rp. ${cart.total}"
        holder.img.setImageResource(cart.image)
        Log.e("CartAdapter", "onBindViewHolder dipanggil dengan position: $position")
        Log.e("CartAdapter", "Name: ${cart.name}")
        Log.e("CartAdapter", "Desc: ${cart.desc}")
        Log.e("CartAdapter", "Addons: ${cart.addons}")
        Log.e("CartAdapter", "Extra: ${cart.extra}")
        Log.e("CartAdapter", "Total: ${cart.total}")
        Log.e("CartAdapter", "Image: ${cart.image}")
        holder.btnOrder.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Lanjut ke pembayaran",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}