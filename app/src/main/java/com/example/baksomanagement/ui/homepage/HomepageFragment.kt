package com.example.baksomanagement.ui.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.repository.MenuRepository
import com.example.baksomanagement.ui.menu.MenuAdapter
import com.example.baksomanagement.ui.orderStatus.OrderSessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomepageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnStatusOrderan: Button

    private val repository = MenuRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_homepage,
            container,
            false
        )

        recyclerView = view.findViewById(R.id.recyclerMenu)

        recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)

        btnStatusOrderan =
            view.findViewById(R.id.btnStatusOrderan)

        loadMenu()

        setupStatusButton()

        return view
    }

    override fun onResume() {
        super.onResume()

        setupStatusButton()
    }

    private fun setupStatusButton() {

        if (OrderSessionManager.lastOrderItems.isNotEmpty()) {

            btnStatusOrderan.visibility = View.VISIBLE

        } else {

            btnStatusOrderan.visibility = View.GONE
        }

        btnStatusOrderan.setOnClickListener {

            findNavController().navigate(
                R.id.action_homepageFragment_to_statusOrderanFragment
            )
        }
    }

    private fun loadMenu() {

        repository.getMenuList { menuList ->

            val adapter = MenuAdapter(menuList) { menu ->

                val bundle = Bundle().apply {

                    putString("MENU_ID", menu.id)
                }

                findNavController().navigate(
                    R.id.action_homepageFragment_to_detailMenuFragment,
                    bundle
                )
            }

            recyclerView.adapter = adapter
        }
    }
}