package com.example.baksomanagement.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.ui.MenuAdapter
import com.example.baksomanagement.data.model.Menu
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baksomanagement.R

class MenuFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerMenu = view.findViewById<RecyclerView>(R.id.recyclerMenu)

        recyclerMenu.layoutManager = LinearLayoutManager(requireContext())

        val menuList = listOf(

            Menu(
                "Bakso Keju",
                "Bakso isi keju mozarella",
                R.drawable.bakso
            ),

            Menu(
                "Bakso Beranak",
                "Bakso isi telur",
                R.drawable.bakso
            ),

            Menu(
                "Bakso Pedas",
                "Bakso dengan sambal pedas",
                R.drawable.bakso
            )

        )

        recyclerMenu.adapter = MenuAdapter(menuList) { menu ->
            val fragment = DetailMenuFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }
}