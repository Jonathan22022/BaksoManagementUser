package com.example.baksomanagement.ui.orderStatus

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R

class StatusOrderanFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvStatus)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val data = OrderSessionManager.lastOrderItems
        rv.adapter = OrderStatusAdapter(data)
    }
}