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
import com.example.baksomanagement.data.model.BahanItem
import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.model.OrderItem
import com.example.baksomanagement.data.repository.AddOnRepository
import com.example.baksomanagement.data.repository.BahanBakuRepository
import com.example.baksomanagement.data.repository.MenuRepository
import com.example.baksomanagement.data.repository.OrderRepository
import com.example.baksomanagement.ui.cart.CartManager
import com.google.firebase.auth.FirebaseAuth

class DetailMenuFragment : Fragment() {

    private val TAG = "DetailMenuDebug"

    private lateinit var menuRepository: MenuRepository
    private lateinit var bahanRepository: BahanBakuRepository

    private var menuId: String = ""

    private var quantity = 1
    private var basePrice = 0
    private var selectedAddonPrice = 0

    private lateinit var tvQty: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvOutOfStock: TextView

    private lateinit var btnOrder: Button

    private var imageUrl: String = ""

    private lateinit var rvAddons: RecyclerView

    private lateinit var addOnRepository: AddOnRepository
    private lateinit var orderRepository: OrderRepository

    private var selectedAddons: MutableList<AddOn> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuId = arguments?.getString("MENU_ID") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_detail_menu,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        menuRepository = MenuRepository()
        bahanRepository = BahanBakuRepository()
        orderRepository = OrderRepository()

        val tvName = view.findViewById<TextView>(R.id.tvMenuName)
        val imgMenu = view.findViewById<ImageView>(R.id.imgMenu)
        val tvDesc = view.findViewById<TextView>(R.id.tvDescription)

        tvQty = view.findViewById(R.id.tvQty)
        tvTotal = view.findViewById(R.id.tvTotal)

        tvOutOfStock =
            view.findViewById(R.id.tvOutOfStock)

        btnOrder =
            view.findViewById(R.id.btnOrder)

        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMinus = view.findViewById<Button>(R.id.btnMinus)

        rvAddons = view.findViewById(R.id.rvAddons)

        rvAddons.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        addOnRepository = AddOnRepository()

        menuRepository.getMenuById(menuId) { menu ->

            menu?.let {

                tvName.text = it.namaMenu

                tvDesc.text =
                    if (it.description.isEmpty())
                        "Menu spesial"
                    else
                        it.description

                imageUrl = it.gambarUrl

                basePrice = it.harga

                updateTotal()

                Glide.with(requireContext())
                    .load(it.gambarUrl)
                    .into(imgMenu)

                checkStock(it.bahanList)
            }
        }

        btnPlus.setOnClickListener {
            quantity++
            tvQty.text = quantity.toString()
            updateTotal()
        }

        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQty.text = quantity.toString()
                updateTotal()
            }
        }

        addOnRepository.getAddOnList { addOnList ->
            val adapter =
                AddOnAdapterDetailMenu(addOnList) { addon, isChecked ->
                    onAddonSelected(addon, isChecked)
                }
            rvAddons.adapter = adapter
        }

        btnOrder.setOnClickListener {
            val totalHarga = (basePrice + selectedAddonPrice) * quantity
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
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
                addons = selectedAddons,
                imageUrl = imageUrl
            )

            CartManager.addItem(item)

            orderRepository.createOrder(order, listOf(item)) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Order ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun checkStock(bahanList: List<BahanItem>) {
        if (bahanList.isEmpty()) {
            btnOrder.visibility = View.VISIBLE
            tvOutOfStock.visibility = View.GONE
            return
        }

        var checkedCount = 0
        var stockAvailable = true

        bahanList.forEach { bahanItem ->
            bahanRepository.getBahanById(bahanItem.bahanId) { bahan ->
                checkedCount++
                if (bahan == null) {
                    stockAvailable = false
                } else {
                    if (bahan.berat < bahanItem.jumlah) {
                        stockAvailable = false
                    }
                }

                if (checkedCount == bahanList.size) {
                    if (stockAvailable) {
                        btnOrder.visibility = View.VISIBLE
                        tvOutOfStock.visibility = View.GONE
                    } else {
                        btnOrder.visibility = View.GONE
                        tvOutOfStock.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun onAddonSelected(addon: AddOn, isChecked: Boolean) {
        if (isChecked) {
            selectedAddons.add(addon)
            selectedAddonPrice += addon.price
        } else {
            selectedAddons.remove(addon)
            selectedAddonPrice -= addon.price
        }
        updateTotal()
    }

    private fun updateTotal() {
        val total = (basePrice + selectedAddonPrice) * quantity
        tvTotal.text = "Rp. $total"
    }
}