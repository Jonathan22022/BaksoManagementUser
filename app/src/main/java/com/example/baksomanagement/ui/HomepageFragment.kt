package com.example.baksomanagement.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.repository.MenuRepository
import com.example.baksomanagement.ui.menu.DetailMenuFragment
import com.example.baksomanagement.ui.menu.MenuAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomepageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val repository = MenuRepository()
    private lateinit var fabStatus: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_homepage, container, false)

        recyclerView = view.findViewById(R.id.recyclerMenu)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        loadMenu()

        fabStatus = view.findViewById(R.id.fabStatus)
        fabStatus.visibility = View.GONE
        if (OrderSessionManager.lastOrderItems.isNotEmpty()) {
            fabStatus.visibility = View.VISIBLE
        }
        fabStatus.setOnClickListener {
            findNavController().navigate(R.id.action_homepageFragment_to_statusOrderanFragment)
        }
        return view
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