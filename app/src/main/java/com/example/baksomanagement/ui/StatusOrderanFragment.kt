package com.example.baksomanagement.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baksomanagement.R
import androidx.recyclerview.widget.RecyclerView

class StatusOrderanFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvStatus)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val data = OrderSessionManager.lastOrderItems
        rv.adapter = OrderStatusAdapter(data)
    }
}