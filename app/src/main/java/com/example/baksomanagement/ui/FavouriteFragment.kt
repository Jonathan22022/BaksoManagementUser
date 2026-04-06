package com.example.baksomanagement.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Favourite

class FavouriteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerFavourite)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        val favouriteList = listOf(
            Favourite("Bakso Keju",R.drawable.bakso),
            Favourite("Bakso Beranak",R.drawable.bakso),
            Favourite("Bakso Urat",R.drawable.bakso),
            Favourite("Bakso Jumbo",R.drawable.bakso)
        )

        recycler.adapter = FavouriteAdapter(favouriteList)

        return view
    }
}