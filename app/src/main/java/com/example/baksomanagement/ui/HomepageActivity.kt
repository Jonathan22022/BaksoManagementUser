package com.example.baksomanagement.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Menu
import com.example.baksomanagement.data.remote.FirebaseClient

class HomepageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val menuList = mutableListOf<Menu>()
    private lateinit var adapter: MenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_homepage, container, false)

        recyclerView = view.findViewById(R.id.recyclerMenu)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = MenuAdapter(menuList) { menu ->
            val fragment = DetailMenuFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter

        loadMenu()

        return view
    }

    private fun loadMenu() {
        FirebaseClient.firestore.collection("Menu")
            .get()
            .addOnSuccessListener { result ->
                menuList.clear()

                for (document in result) {
                    val menu = document.toObject(Menu::class.java)
                    menuList.add(menu)
                }

                adapter.notifyDataSetChanged()
            }
    }
}