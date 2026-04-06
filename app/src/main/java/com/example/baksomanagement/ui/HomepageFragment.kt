package com.example.baksomanagement.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Menu

class HomepageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerMenu)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val menuList = listOf(
            Menu("Bakso Keju", "Bakso isi keju mozarella", R.drawable.bakso),
            Menu("Bakso Beranak", "Bakso isi telur", R.drawable.bakso),
            Menu("Bakso Keju", "Bakso isi keju mozarella", R.drawable.bakso),
            Menu("Bakso Beranak", "Bakso isi telur", R.drawable.bakso)
        )

        val adapter = MenuAdapter(menuList) { menu ->
            // pindah ke detail menu
            val fragment = DetailMenuFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
        return view
    }
}