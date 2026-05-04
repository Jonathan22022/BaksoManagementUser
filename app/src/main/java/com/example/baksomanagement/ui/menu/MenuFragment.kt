package com.example.baksomanagement.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import android.widget.Button
import com.example.baksomanagement.data.model.Menu
import com.example.baksomanagement.data.repository.MenuRepository
import com.example.baksomanagement.ui.menu.DetailMenuFragment
import com.example.baksomanagement.ui.cart.CartManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var menuRepository: MenuRepository
    private lateinit var btnCart: Button

    private val TAG = "MenuFragmentDebug"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView called")
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated called")

        recyclerView = view.findViewById(R.id.recyclerMenu)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        menuRepository = MenuRepository()
        Log.d(TAG, "MenuRepository initialized")

        loadMenu()

        btnCart = view.findViewById(R.id.btnCart)

        btnCart.setOnClickListener {
            Log.d(TAG, "Cart button clicked → navigate to checkout")
            findNavController().navigate(R.id.action_menuFragment_to_checkoutFragment)
        }
        updateCartButton()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called → updating badge")
        updateCartButton()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun loadMenu() {
        Log.d(TAG, "loadMenu() called")

        menuRepository.getMenuList { menuList ->

            Log.d(TAG, "Menu loaded. Total items: ${menuList.size}")

            if (menuList.isEmpty()) {
                Log.e(TAG, "Menu list is EMPTY!")
            } else {
                menuList.forEach {
                    Log.d(TAG, "Menu item → id: ${it.id}, name: ${it.namaMenu}, harga: ${it.harga}")
                }
            }

            val adapter = MenuAdapter(menuList) { selectedMenu ->
                Log.d(TAG, "Menu clicked → ${selectedMenu.namaMenu}")
                openDetailMenu(selectedMenu)
            }

            recyclerView.adapter = adapter
            Log.d(TAG, "RecyclerView adapter set")
        }
    }

    private fun openDetailMenu(menu: Menu) {
        Log.d(TAG, "openDetailMenu() → MENU_ID: ${menu.id}")

        val bundle = Bundle().apply {
            putString("MENU_ID", menu.id)
        }

        findNavController().navigate(
            R.id.action_menuFragment_to_detailMenuFragment,
            bundle
        )
    }

    private fun updateCartButton() {
        val total = CartManager.getTotalQuantity()

        Log.d(TAG, "updateCartButton() → total item: $total")

        if (total > 0) {
            btnCart.visibility = View.VISIBLE
            btnCart.text = "Lihat Keranjang ($total)"
        } else {
            btnCart.visibility = View.GONE
        }
    }
}