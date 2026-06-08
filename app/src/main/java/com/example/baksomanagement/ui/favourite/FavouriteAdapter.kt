package com.example.baksomanagement.ui.favourite

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
import com.example.baksomanagement.data.model.Favourite

class FavouriteAdapter(
    private val list: List<Favourite>,
    private val onClick: (Favourite) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "FavouriteAdapter"
    }

    init {
        Log.d(TAG, "Adapter initialized")
        Log.d(TAG, "Total favourite items = ${list.size}")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val img: ImageView = view.findViewById(R.id.imgMenu)
        val name: TextView = view.findViewById(R.id.tvMenuName)
        val star: ImageView = view.findViewById(R.id.imgFav)
        val btn: Button = view.findViewById(R.id.btnMenu)

        init {
            Log.d(TAG, "ViewHolder created")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        Log.d(TAG, "onCreateViewHolder() called")

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_favourite,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        Log.d(TAG, "getItemCount() = ${list.size}")

        return list.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        Log.d(
            TAG,
            """
            onBindViewHolder()
            position = $position
            menuId = ${item.menuId}
            namaMenu = ${item.namaMenu}
            gambarUrl = ${item.gambarUrl}
            """.trimIndent()
        )

        holder.name.text = item.namaMenu

        if (item.gambarUrl.isBlank()) {
            Log.w(
                TAG,
                "Image URL kosong untuk menu: ${item.namaMenu}"
            )
        } else {
            Log.d(
                TAG,
                "Loading image: ${item.gambarUrl}"
            )
        }

        Glide.with(holder.itemView.context)
            .load(item.gambarUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.img)

        holder.itemView.setOnClickListener {

            Log.d(
                TAG,
                """
                Item clicked
                position = $position
                menuId = ${item.menuId}
                namaMenu = ${item.namaMenu}
                """.trimIndent()
            )

            onClick(item)
        }

        holder.btn.setOnClickListener {

            Log.d(
                TAG,
                """
                Button clicked
                position = $position
                menuId = ${item.menuId}
                namaMenu = ${item.namaMenu}
                """.trimIndent()
            )

            onClick(item)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)

        Log.d(
            TAG,
            "View recycled at position = ${holder.adapterPosition}"
        )
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        Log.d(TAG, "Adapter attached to RecyclerView")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        Log.d(TAG, "Adapter detached from RecyclerView")
    }
}