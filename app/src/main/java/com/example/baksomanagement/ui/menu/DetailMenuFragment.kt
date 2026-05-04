package com.example.baksomanagement.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.AddOn
import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.model.OrderItem
import com.example.baksomanagement.data.repository.AddOnRepository
import com.example.baksomanagement.data.repository.MenuRepository
import com.example.baksomanagement.data.repository.OrderRepository
import com.example.baksomanagement.ui.cart.CartManager
import com.google.firebase.auth.FirebaseAuth

class DetailMenuFragment : Fragment() {

    private val TAG = "DetailMenuDebug"

    private lateinit var menuRepository: MenuRepository
    private var menuId: String = ""

    private var quantity = 1
    private var basePrice = 0
    private var selectedAddonPrice = 0

    private lateinit var tvQty: TextView
    private lateinit var tvTotal: TextView
    private lateinit var layoutAddons: LinearLayout
    private lateinit var btnEditMenu: Button
    private lateinit var orderRepository: OrderRepository
    private lateinit var rvAddons: RecyclerView
    private lateinit var addOnRepository: AddOnRepository
    private var selectedAddons: MutableList<AddOn> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuId = arguments?.getString("MENU_ID") ?: ""

        Log.d(TAG, "onCreate → MENU_ID: $menuId")

        if (menuId.isEmpty()) {
            Log.e(TAG, "MENU_ID is EMPTY! Navigation data might be missing")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView called")
        return inflater.inflate(R.layout.fragment_detail_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated called")

        orderRepository = OrderRepository()
        menuRepository = MenuRepository()

        val tvName = view.findViewById<TextView>(R.id.tvMenuName)
        val imgMenu = view.findViewById<ImageView>(R.id.imgMenu)
        val tvDesc = view.findViewById<TextView>(R.id.tvDescription)

        tvQty = view.findViewById(R.id.tvQty)
        tvTotal = view.findViewById(R.id.tvTotal)

        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMinus = view.findViewById<Button>(R.id.btnMinus)

        Log.d(TAG, "Fetching menu by ID: $menuId")

        rvAddons = view.findViewById(R.id.rvAddons)
        rvAddons.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        addOnRepository = AddOnRepository()

        menuRepository.getMenuById(menuId) { menu ->

            if (menu == null) {
                Log.e(TAG, "Menu NOT FOUND for ID: $menuId")
            }

            menu?.let {
                Log.d(TAG, "Menu loaded → ${it.namaMenu}, harga: ${it.harga}")

                tvName.text = it.namaMenu
                tvDesc.text = "Menu spesial pilihan"

                basePrice = it.harga
                Log.d(TAG, "Base price set: $basePrice")

                updateTotal()

                if (it.gambarUrl.isEmpty()) {
                    Log.e(TAG, "Image URL EMPTY")
                } else {
                    Log.d(TAG, "Loading image: ${it.gambarUrl}")
                }

                Glide.with(requireContext())
                    .load(it.gambarUrl)
                    .into(imgMenu)
            }
        }

        // PLUS
        btnPlus.setOnClickListener {
            quantity++
            tvQty.text = quantity.toString()

            Log.d(TAG, "Quantity increased → $quantity")

            updateTotal()
        }

        // MINUS
        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQty.text = quantity.toString()

                Log.d(TAG, "Quantity decreased → $quantity")

                updateTotal()
            } else {
                Log.d(TAG, "Quantity already minimum (1)")
            }
        }

        addOnRepository.getAddOnList { addOnList ->

            Log.d(TAG, "AddOn loaded → ${addOnList.size}")

            val adapter = AddOnAdapterDetailMenu(addOnList) { addon, isChecked ->
                onAddonSelected(addon, isChecked)
            }

            rvAddons.adapter = adapter
        }

        val btnOrder = view.findViewById<Button>(R.id.btnOrder)

        btnOrder.setOnClickListener {

            val totalHarga = (basePrice + selectedAddonPrice) * quantity
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

            Log.d(TAG, "Order button clicked")
            Log.d(TAG, "BasePrice: $basePrice, AddonPrice: $selectedAddonPrice, Qty: $quantity")
            Log.d(TAG, "Total harga: $totalHarga")
            Log.d(TAG, "Selected addons count: ${selectedAddons.size}")

            val order = Order(
                userID = userId,
                total = totalHarga,
                status = "pending"
            )

            val item = OrderItem(
                menu_id = menuId,
                nama = tvName.text.toString(),
                harga = basePrice,
                quantity = quantity,
                catatan = "",
                addons = selectedAddons
            )

            Log.d(TAG, "Adding item to CartManager")
            CartManager.addItem(item)

            Log.d(TAG, "Sending order to repository")

            orderRepository.createOrder(order, listOf(item)) {
                requireActivity().runOnUiThread {
                    Log.d(TAG, "Order successfully created")

                    Toast.makeText(requireContext(), "Order ditambahkan", Toast.LENGTH_SHORT).show()
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("UPDATE_CART", true)
                    findNavController().popBackStack()                }
            }
        }
    }

    fun onAddonSelected(addon: AddOn, isChecked: Boolean) {
        if (isChecked) {
            selectedAddons.add(addon)
            selectedAddonPrice += addon.price
            Log.d(TAG, "Addon added → ${addon.name}, price: ${addon.price}")
        } else {
            selectedAddons.remove(addon)
            selectedAddonPrice -= addon.price
            Log.d(TAG, "Addon removed → ${addon.name}, price: ${addon.price}")
        }

        Log.d(TAG, "Total addon price now: $selectedAddonPrice")

        updateTotal()
    }

    private fun updateTotal() {
        val total = (basePrice + selectedAddonPrice) * quantity

        Log.d(TAG, "updateTotal() → $total")

        tvTotal.text = "Rp. $total"
    }
}