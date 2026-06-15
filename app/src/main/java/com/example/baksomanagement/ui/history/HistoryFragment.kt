package com.example.baksomanagement.ui.history

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.History
import com.example.baksomanagement.data.repository.HistoryRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HistoryFragment :
    Fragment(R.layout.fragment_history) {

    private lateinit var recycler: RecyclerView

    private lateinit var btnCompleted: TextView

    private lateinit var btnCancelled: TextView

    private lateinit var adapter: HistoryAdapter
    private lateinit var tvDate: EditText
    private lateinit var fabDelete: FloatingActionButton
    private var fullHistory = mutableListOf<History>()

    private val repository =
        HistoryRepository()

    private var currentStatus =
        "selesai"

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        recycler =
            view.findViewById(
                R.id.recyclerHistory
            )

        tvDate = view.findViewById(R.id.tvDate)
        tvDate.setOnClickListener {
            showDatePicker()
        }
        fabDelete = view.findViewById(R.id.fabDelete)
        fabDelete.visibility = View.GONE
        btnCompleted =
            view.findViewById(
                R.id.btnCompleted
            )

        btnCancelled =
            view.findViewById(
                R.id.btnCancelled
            )

        recycler.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        loadHistory()

        btnCompleted.setOnClickListener {
            currentStatus = "selesai"
            switchTab(true)
            loadHistory()
        }

        btnCancelled.setOnClickListener {
            currentStatus = "cancel"
            switchTab(false)
            loadHistory()
        }

        tvDate.setOnLongClickListener {

            tvDate.setText("")

            filterHistory()

            true
        }

        fabDelete.setOnClickListener {

            val selected =
                adapter.getSelectedItems()

            fullHistory.removeAll(selected)

            filterHistory()

            fabDelete.visibility =
                View.GONE
        }
    }

    private fun showDatePicker() {

        val calendar =
            java.util.Calendar.getInstance()

        android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, day ->

                val selected =
                    java.util.Calendar.getInstance()

                selected.set(
                    year,
                    month,
                    day
                )

                val sdf =
                    java.text.SimpleDateFormat(
                        "dd/MM/yyyy",
                        java.util.Locale.getDefault()
                    )

                tvDate.setText(
                    sdf.format(
                        selected.time
                    )
                )

                filterHistory()
            },
            calendar.get(
                java.util.Calendar.YEAR
            ),
            calendar.get(
                java.util.Calendar.MONTH
            ),
            calendar.get(
                java.util.Calendar.DAY_OF_MONTH
            )
        ).show()
    }

    private fun filterHistory() {

        val selectedDate =
            tvDate.text.toString()

        val dateFormat =
            java.text.SimpleDateFormat(
                "dd/MM/yyyy",
                java.util.Locale.getDefault()
            )

        val filtered =
            fullHistory.filter {

                val orderDate =
                    dateFormat.format(
                        java.util.Date(
                            it.createdAt
                        )
                    )

                selectedDate.isEmpty() ||
                        orderDate == selectedDate
            }

        adapter =
            HistoryAdapter(
                filtered.toMutableList(),
                currentStatus
            ){
                updateFabVisibility()
            }

        recycler.adapter =
            adapter
    }

    private fun updateFabVisibility(){

        val selected =
            adapter.getSelectedItems()

        fabDelete.visibility =
            if(selected.isNotEmpty())
                View.VISIBLE
            else
                View.GONE
    }
    private fun loadHistory() {

        repository.getHistoryOrders(
            currentStatus
        ){ list ->

            fullHistory.clear()

            fullHistory.addAll(list)

            filterHistory()
        }
    }

    private fun switchTab(isSelesai: Boolean) {

        if (isSelesai) {

            btnCompleted.setBackgroundResource(R.drawable.bg_tab_active)
            btnCancelled.setBackgroundResource(android.R.color.transparent)

            btnCompleted.setTextColor(resources.getColor(android.R.color.white))
            btnCancelled.setTextColor(resources.getColor(R.color.dark_red))

        } else {

            btnCancelled.setBackgroundResource(R.drawable.bg_tab_active)
            btnCompleted.setBackgroundResource(android.R.color.transparent)

            btnCancelled.setTextColor(resources.getColor(android.R.color.white))
            btnCompleted.setTextColor(resources.getColor(R.color.dark_red))
        }
    }
}