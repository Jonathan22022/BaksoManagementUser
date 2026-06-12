package com.example.baksomanagement.ui.homepage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.repository.FavouriteRepository
import com.example.baksomanagement.data.repository.MenuRepository
import com.example.baksomanagement.data.repository.OrderRepository
import com.example.baksomanagement.ui.menu.MenuAdapter
import com.example.baksomanagement.ui.orderStatus.OrderSessionManager
import com.google.firebase.auth.FirebaseAuth

class HomepageFragment : Fragment() {

    companion object {
        private const val TAG = "HomepageDebug"
    }

    private lateinit var recyclerMostPopular: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerMostFav: RecyclerView
    private lateinit var btnFabStatus: Button

    private val repository = MenuRepository()
    private val orderRepository = OrderRepository()
    private val favouriteRepository = FavouriteRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "==============================")
        Log.d(TAG, "HomepageFragment -> onCreate")
        Log.d(TAG, "==============================")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "HomepageFragment -> onCreateView")

        val view =
            inflater.inflate(
                R.layout.fragment_homepage,
                container,
                false
            )

        recyclerView =
            view.findViewById(R.id.recyclerMenu)

        recyclerMostFav =
            view.findViewById(R.id.recyclerMostFav)

        recyclerMostPopular =
            view.findViewById(R.id.recyclerMostPopular)

        btnFabStatus =
            view.findViewById(R.id.btnFabStatus)

        recyclerView.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        recyclerMostFav.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        recyclerMostPopular.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        Log.d(TAG, "RecyclerView berhasil diinisialisasi")

        loadPreviousOrdered()
        loadMostFavouriteMenu()
        loadMostPopularMenu()
        setupStatusButton()

        return view
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "HomepageFragment -> onResume")

        setupStatusButton()
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "HomepageFragment -> onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        Log.d(TAG, "HomepageFragment -> onDestroyView")
    }

    private fun setupStatusButton() {

        Log.d(TAG, "setupStatusButton() dipanggil")

        val userId =
            FirebaseAuth.getInstance()
                .currentUser?.uid

        Log.d(TAG, "Current User ID = $userId")

        if (userId == null) {

            Log.e(TAG, "User belum login")
            return
        }

        orderRepository.getActiveOrder(userId) { order ->

            Log.d(
                TAG,
                "Hasil getActiveOrder = ${order?.id}"
            )

            requireActivity().runOnUiThread {

                if (order != null) {

                    Log.d(
                        TAG,
                        "Order aktif ditemukan"
                    )

                    OrderSessionManager.lastOrderId = order.id

                    btnFabStatus.visibility =
                        View.VISIBLE

                    btnFabStatus.setOnClickListener {

                        Log.d(
                            TAG,
                            "Button Status diklik"
                        )

                        Log.d(
                            TAG,
                            "ORDER_ID = ${order.id}"
                        )

                        val bundle = Bundle().apply {

                            putString(
                                "ORDER_ID",
                                order.id
                            )
                        }

                        findNavController().navigate(
                            R.id.action_homepageFragment_to_statusOrderanFragment,
                            bundle
                        )
                    }

                } else {

                    Log.d(
                        TAG,
                        "Tidak ada order aktif"
                    )

                    btnFabStatus.visibility =
                        View.GONE
                }
            }
        }
    }

    private fun updateRecyclerHeight(
        recyclerView: RecyclerView,
        isEmpty: Boolean
    ) {

        Log.d(
            TAG,
            "updateRecyclerHeight() -> isEmpty = $isEmpty"
        )

        val params =
            recyclerView.layoutParams

        params.height =
            if (isEmpty) {

                Log.d(
                    TAG,
                    "Menggunakan tinggi kosong"
                )

                (260 * resources.displayMetrics.density)
                    .toInt()

            } else {

                Log.d(
                    TAG,
                    "Menggunakan WRAP_CONTENT"
                )

                ViewGroup.LayoutParams.WRAP_CONTENT
            }

        recyclerView.layoutParams = params
    }

    private fun loadMostFavouriteMenu() {

        Log.d(TAG, "========== LOAD MOST FAVOURITE ==========")

        favouriteRepository.getTopFavouriteMenus { topIds ->

            Log.d(
                TAG,
                "Top Favourite IDs = $topIds"
            )

            Log.d(
                TAG,
                "Jumlah Favourite = ${topIds.size}"
            )

            if (topIds.isEmpty()) {

                Log.d(
                    TAG,
                    "Data favourite kosong"
                )

                updateRecyclerHeight(
                    recyclerMostFav,
                    true
                )

                recyclerMostFav.adapter =
                    MenuAdapter(emptyList()) { }

                return@getTopFavouriteMenus
            }

            repository.getMenuList { menuList ->

                Log.d(
                    TAG,
                    "Total menu database = ${menuList.size}"
                )

                val topMenus =
                    menuList.filter {
                        topIds.contains(it.id)
                    }

                Log.d(
                    TAG,
                    "Menu favourite ditemukan = ${topMenus.size}"
                )

                requireActivity().runOnUiThread {

                    updateRecyclerHeight(
                        recyclerMostFav,
                        false
                    )

                    recyclerMostFav.adapter =
                        MenuAdapter(topMenus) { menu ->

                            Log.d(
                                TAG,
                                "Klik Menu Favourite"
                            )

                            Log.d(
                                TAG,
                                "MENU_ID = ${menu.id}"
                            )

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
                }
            }
        }
    }

    private fun loadMostPopularMenu() {

        Log.d(TAG, "========== LOAD MOST POPULAR ==========")

        orderRepository.getTopPopularMenus { topIds ->

            Log.d(
                TAG,
                "Top Popular IDs = $topIds"
            )

            Log.d(
                TAG,
                "Jumlah Popular = ${topIds.size}"
            )

            if (topIds.isEmpty()) {

                Log.d(
                    TAG,
                    "Data popular kosong"
                )

                updateRecyclerHeight(
                    recyclerMostPopular,
                    true
                )

                recyclerMostPopular.adapter =
                    MenuAdapter(emptyList()) { }

                return@getTopPopularMenus
            }

            repository.getMenuList { menuList ->

                Log.d(
                    TAG,
                    "Total menu database = ${menuList.size}"
                )

                val topMenus =
                    topIds.mapNotNull { id ->
                        menuList.find {
                            it.id == id
                        }
                    }

                Log.d(
                    TAG,
                    "Menu popular ditemukan = ${topMenus.size}"
                )

                requireActivity().runOnUiThread {

                    updateRecyclerHeight(
                        recyclerMostPopular,
                        false
                    )

                    recyclerMostPopular.adapter =
                        MenuAdapter(topMenus) { menu ->

                            Log.d(
                                TAG,
                                "Klik Menu Popular"
                            )

                            Log.d(
                                TAG,
                                "MENU_ID = ${menu.id}"
                            )

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
                }
            }
        }
    }

    private fun loadPreviousOrdered() {

        Log.d(TAG, "========== LOAD PREVIOUS ORDER ==========")

        val userId =
            FirebaseAuth.getInstance()
                .currentUser?.uid

        Log.d(TAG, "User ID = $userId")

        if (userId == null) {

            Log.e(
                TAG,
                "User belum login"
            )

            return
        }

        orderRepository.getFinishedOrders(userId) { menuIds ->

            Log.d(
                TAG,
                "Finished Order Menu IDs = $menuIds"
            )

            Log.d(
                TAG,
                "Jumlah menu pernah dipesan = ${menuIds.size}"
            )

            if (menuIds.isEmpty()) {

                Log.d(
                    TAG,
                    "Belum ada riwayat pesanan"
                )

                updateRecyclerHeight(
                    recyclerView,
                    true
                )

                recyclerView.adapter =
                    MenuAdapter(emptyList()) { }

                return@getFinishedOrders
            }

            repository.getMenuList { menuList ->

                Log.d(
                    TAG,
                    "Total menu database = ${menuList.size}"
                )

                val previousMenus =
                    menuIds.mapNotNull { id ->
                        menuList.find {
                            it.id == id
                        }
                    }

                Log.d(
                    TAG,
                    "Previous menu ditemukan = ${previousMenus.size}"
                )

                requireActivity().runOnUiThread {

                    updateRecyclerHeight(
                        recyclerView,
                        false
                    )

                    recyclerView.adapter =
                        MenuAdapter(previousMenus) { menu ->

                            Log.d(
                                TAG,
                                "Klik Previous Ordered Menu"
                            )

                            Log.d(
                                TAG,
                                "MENU_ID = ${menu.id}"
                            )

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
                }
            }
        }
    }
}