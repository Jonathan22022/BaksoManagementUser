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
import com.example.baksomanagement.data.repository.FavouriteRepository
import com.example.baksomanagement.data.model.Menu
import com.example.baksomanagement.data.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager

class HomepageFragment : Fragment() {
    private lateinit var recyclerMostPopular: RecyclerView
    private val orderRepository = OrderRepository()
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnFabStatus: Button
    private lateinit var recyclerMostFav: RecyclerView
    private val favouriteRepository = FavouriteRepository()
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
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerMostFav = view.findViewById(R.id.recyclerMostFav)
        recyclerMostFav.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerMostPopular = view.findViewById(R.id.recyclerMostPopular)
        recyclerMostPopular.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        btnFabStatus = view.findViewById(R.id.btnFabStatus)
        loadPreviousOrdered()
        loadMostFavouriteMenu()
        loadMostPopularMenu()
        setupStatusButton()

        return view
    }

    override fun onResume() {
        super.onResume()

        setupStatusButton()
    }

    private fun setupStatusButton() {

        Log.e(
            "HomepageDebug",
            "Item session = ${OrderSessionManager.lastOrderItems.size}"
        )

        val hasOrder =
            OrderSessionManager.lastOrderItems.isNotEmpty()

        Log.e(
            "HomepageDebug",
            "hasOrder = $hasOrder"
        )

        if (hasOrder) {

            btnFabStatus.visibility = View.VISIBLE

            Log.e(
                "HomepageDebug",
                "BUTTON STATUS MUNCUL"
            )

            btnFabStatus.setOnClickListener {

                Log.e(
                    "HomepageDebug",
                    "BUTTON STATUS DIKLIK"
                )

                findNavController().navigate(
                    R.id.action_homepageFragment_to_statusOrderanFragment
                )
            }

        } else {

            btnFabStatus.visibility = View.GONE

            Log.e(
                "HomepageDebug",
                "BUTTON STATUS HILANG"
            )
        }
    }

    private fun updateRecyclerHeight(
        recyclerView: RecyclerView,
        isEmpty: Boolean
    ) {
        val params = recyclerView.layoutParams

        params.height =
            if (isEmpty) {
                (260 * resources.displayMetrics.density).toInt()
            } else {
                ViewGroup.LayoutParams.WRAP_CONTENT
            }

        recyclerView.layoutParams = params
    }

    private fun loadMostFavouriteMenu() {

        favouriteRepository.getTopFavouriteMenus { topIds ->

            if (topIds.isEmpty()) {

                updateRecyclerHeight(recyclerMostFav, true)
                recyclerMostFav.adapter = MenuAdapter(emptyList()) { }

                return@getTopFavouriteMenus
            }

            repository.getMenuList { menuList ->

                val topMenus = menuList.filter {
                    topIds.contains(it.id)
                }

                requireActivity().runOnUiThread {
                    updateRecyclerHeight(recyclerMostFav, false)
                    val adapter = MenuAdapter(topMenus) { menu ->

                            val bundle = Bundle().apply {
                                putString("MENU_ID", menu.id)
                            }

                            findNavController().navigate(
                                R.id.action_homepageFragment_to_detailMenuFragment,
                                bundle
                            )
                        }

                    recyclerMostFav.adapter = adapter
                }
            }
        }
    }

    private fun loadMostPopularMenu() {

        orderRepository.getTopPopularMenus { topIds ->

            if (topIds.isEmpty()) {

                updateRecyclerHeight(recyclerMostPopular, true)
                recyclerMostPopular.adapter = MenuAdapter(emptyList()) { }

                return@getTopPopularMenus
            }

            repository.getMenuList { menuList ->

                val topMenus =
                    topIds.mapNotNull { id ->

                        menuList.find {
                            it.id == id
                        }
                    }

                requireActivity().runOnUiThread {
                    updateRecyclerHeight(recyclerMostPopular, false)
                    val adapter =
                        MenuAdapter(topMenus) { menu ->

                            val bundle = Bundle().apply {

                                putString(
                                    "MENU_ID",
                                    menu.id
                                )
                            }

                            findNavController().navigate(
                                R.id.action_homepageFragment_to_detailMenuFragment,
                                bundle
                            )
                        }

                    recyclerMostPopular.adapter =
                        adapter
                }
            }
        }
    }

    private fun loadPreviousOrdered() {

        val userId =
            FirebaseAuth.getInstance()
                .currentUser?.uid ?: return

        orderRepository.getFinishedOrders(userId) { menuIds ->

            if (menuIds.isEmpty()) {

                updateRecyclerHeight(recyclerView, true)
                recyclerView.adapter = MenuAdapter(emptyList()) { }

                return@getFinishedOrders
            }

            repository.getMenuList { menuList ->

                val previousMenus =
                    menuIds.mapNotNull { id ->

                        menuList.find {
                            it.id == id
                        }
                    }

                requireActivity().runOnUiThread {

                    updateRecyclerHeight(recyclerView, false)

                    val adapter = MenuAdapter(previousMenus) { menu ->

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
    }
}