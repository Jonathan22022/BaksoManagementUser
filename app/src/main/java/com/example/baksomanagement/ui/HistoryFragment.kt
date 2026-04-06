package com.example.baksomanagement.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baksomanagement.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.data.model.History
import com.example.baksomanagement.ui.HistoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog
import android.widget.Toast

class HistoryFragment : Fragment() {

    private lateinit var adapter: HistoryAdapter
    private val historyList = mutableListOf<History>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_history, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerHistory)
        val fabDelete = view.findViewById<FloatingActionButton>(R.id.fabDelete)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        historyList.addAll(
            listOf(
                History("Bakso Keju","Senin, 24 Januari 2025","Campuran Mie dan Bihun","Rp. 28.000",R.drawable.bakso,"Completed"),
                History("Bakso Beranak","Senin, 24 Januari 2025","No addon","Rp. 20.000",R.drawable.bakso,"Completed"),
                History("Bakso Keju","Senin, 24 Januari 2025","No addon","Rp. 28.000",R.drawable.bakso,"Cancelled")
            )
        )

        adapter = HistoryAdapter(historyList)
        recycler.adapter = adapter

        // FAB DELETE
        fabDelete.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("Hapus History")
                .setMessage("Apakah kamu yakin ingin menghapus semua history?")
                .setPositiveButton("Hapus") { _, _ ->

                    historyList.clear()
                    adapter.notifyDataSetChanged()

                    Toast.makeText(
                        requireContext(),
                        "History berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        return view
    }
}