package com.example.baksomanagement.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.AddOn
import com.example.baksomanagement.data.model.BahanItem
import com.example.baksomanagement.data.model.Favourite
import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.model.OrderItem
import com.example.baksomanagement.data.repository.AddOnRepository
import com.example.baksomanagement.data.repository.BahanBakuRepository
import com.example.baksomanagement.data.repository.FavouriteRepository
import com.example.baksomanagement.data.repository.MenuRepository
import com.example.baksomanagement.data.repository.OrderRepository
import com.example.baksomanagement.ui.cart.CartManager
import com.google.firebase.auth.FirebaseAuth

class DetailMenuFragment : Fragment() {

    private val TAG = "DetailMenuDebug"
    private var editPosition = -1
    private var isEditMode = false

    private lateinit var btnUpdateOrder: Button
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

    private val addonStockMap = mutableMapOf<String, Boolean>()

    private lateinit var rvAddons: RecyclerView
    private lateinit var favouriteRepository: FavouriteRepository
    private lateinit var imgFavourite: ImageView
    private lateinit var addOnRepository: AddOnRepository
    private lateinit var orderRepository: OrderRepository

    private var isFavourite = false

    private var selectedAddons: MutableList<AddOn> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuId = arguments?.getString("MENU_ID") ?: ""

        editPosition =
            arguments?.getInt(
                "EDIT_POSITION",
                -1
            ) ?: -1

        isEditMode = editPosition != -1

        Log.d(TAG, "onCreate")
        Log.d(TAG, "MENU_ID = $menuId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView")

        return inflater.inflate(
            R.layout.fragment_detail_menu,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d(TAG, "onViewCreated")

        menuRepository = MenuRepository()
        bahanRepository = BahanBakuRepository()
        orderRepository = OrderRepository()
        favouriteRepository = FavouriteRepository()

        imgFavourite = view.findViewById(R.id.imgFavourite)

        val tvName = view.findViewById<TextView>(R.id.tvMenuName)
        val imgMenu = view.findViewById<ImageView>(R.id.imgMenu)
        val tvDesc = view.findViewById<TextView>(R.id.tvDescription)
        val tvPrice = view.findViewById<TextView>(R.id.tvMenuPrice)
        tvQty = view.findViewById(R.id.tvQty)
        tvTotal = view.findViewById(R.id.tvTotal)
        tvOutOfStock = view.findViewById(R.id.tvOutOfStock)
        btnOrder = view.findViewById(R.id.btnOrder)
        btnUpdateOrder = view.findViewById(R.id.btnUpdateOrder)
        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMinus = view.findViewById<Button>(R.id.btnMinus)

        rvAddons = view.findViewById(R.id.rvAddons)

        rvAddons.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        addOnRepository = AddOnRepository()

        Log.d(TAG, "Mengambil data menu: $menuId")

        menuRepository.getMenuById(menuId) { menu ->

            if (menu == null) {
                Log.e(TAG, "Menu tidak ditemukan")
                return@getMenuById
            }

            tvPrice.text = "Rp ${menu.harga}"

            favouriteRepository.isFavourite(menuId) { favourite ->

                isFavourite = favourite

                if (favourite) {
                    imgFavourite.setImageResource(R.drawable.ic_star_on)
                } else {
                    imgFavourite.setImageResource(R.drawable.ic_star_off)
                }
            }

            imgFavourite.setOnClickListener {

                if (isFavourite) {

                    favouriteRepository.removeFavourite(menu.id) { success ->

                        if (success) {

                            isFavourite = false

                            imgFavourite.setImageResource(
                                R.drawable.ic_star_off
                            )

                            Toast.makeText(
                                requireContext(),
                                "Dihapus dari Favourite",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {

                    val favourite = Favourite(
                        menuId = menu.id,
                        namaMenu = menu.namaMenu,
                        gambarUrl = menu.gambarUrl
                    )

                    favouriteRepository.addFavourite(
                        favourite
                    ) { success ->

                        if (success) {

                            isFavourite = true

                            imgFavourite.setImageResource(
                                R.drawable.ic_star_on
                            )

                            Toast.makeText(
                                requireContext(),
                                "Ditambahkan ke Favourite",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            tvName.text = menu.namaMenu
            tvDesc.text = menu.description

            imageUrl = menu.gambarUrl
            basePrice = menu.harga

            if (isEditMode) {

                val oldItem =
                    CartManager.items[editPosition]

                quantity = oldItem.quantity

                tvQty.text =
                    quantity.toString()

                selectedAddons.clear()

                selectedAddons.addAll(
                    oldItem.addons
                )

                selectedAddonPrice =
                    oldItem.addons.sumOf {
                        it.price
                    }

                updateTotal()

                btnOrder.visibility =
                    View.GONE

                btnUpdateOrder.visibility =
                    View.VISIBLE
            }

            updateTotal()

            Glide.with(requireContext())
                .load(menu.gambarUrl)
                .into(imgMenu)

            checkStock(menu.bahanList)
        }

        btnPlus.setOnClickListener {

            quantity++

            Log.d(TAG, "Quantity ditambah -> $quantity")

            tvQty.text = quantity.toString()

            updateTotal()
        }

        btnMinus.setOnClickListener {

            if (quantity > 1) {

                quantity--

                Log.d(TAG, "Quantity dikurangi -> $quantity")

                tvQty.text = quantity.toString()

                updateTotal()
            }
        }

        btnUpdateOrder.setOnClickListener {

            val updatedItem =
                OrderItem(
                    menu_id = menuId,
                    nama = tvName.text.toString(),
                    harga = basePrice,
                    quantity = quantity,
                    catatan = "",
                    addons = selectedAddons.toMutableList(),
                    imageUrl = imageUrl
                )

            CartManager.updateItem(
                editPosition,
                updatedItem
            )

            Toast.makeText(
                requireContext(),
                "Pesanan berhasil diubah",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack(
                R.id.checkoutFragment,
                false
            )
        }

        addOnRepository.getAddOnList { addOnList ->

            Log.d(TAG, "Total AddOn ditemukan = ${addOnList.size}")

            if (addOnList.isEmpty()) {
                Log.w(TAG, "AddOn kosong")
                return@getAddOnList
            }

            var checked = 0

            addOnList.forEach { addon ->

                Log.d(
                    TAG,
                    "Cek stok addon -> ${addon.name}"
                )

                checkAddonStock(addon) { available ->

                    Log.d(
                        TAG,
                        "Addon ${addon.name} available = $available"
                    )

                    addonStockMap[addon.id] = available

                    checked++

                    if (checked == addOnList.size) {

                        Log.d(
                            TAG,
                            "Semua stok addon selesai dicek"
                        )

                        val adapter =
                            AddOnAdapterDetailMenu(
                                addOnList,
                                addonStockMap,
                                selectedAddons
                            ) { selectedAddon, isChecked ->

                                onAddonSelected(
                                    selectedAddon,
                                    isChecked
                                )
                            }

                        rvAddons.adapter = adapter
                    }
                }
            }
        }

        if (!isEditMode) {
            btnOrder.setOnClickListener {

                val totalHarga =
                    (basePrice + selectedAddonPrice) * quantity

                val userId =
                    FirebaseAuth.getInstance()
                        .currentUser?.uid ?: "guest"

                Log.d(TAG, "=== ORDER ===")
                Log.d(TAG, "User ID = $userId")
                Log.d(TAG, "Menu ID = $menuId")
                Log.d(TAG, "Qty = $quantity")
                Log.d(TAG, "Base Price = $basePrice")
                Log.d(TAG, "Addon Price = $selectedAddonPrice")
                Log.d(TAG, "Total = $totalHarga")
                Log.d(TAG, "Addon Count = ${selectedAddons.size}")

                val item = OrderItem(
                    menu_id = menuId,
                    nama = tvName.text.toString(),
                    harga = basePrice,
                    quantity = quantity,
                    catatan = "",
                    addons = selectedAddons,
                    imageUrl = imageUrl
                )

                Log.d(TAG, "Menambahkan item ke CartManager")

                CartManager.addItem(item)

                Toast.makeText(
                    requireContext(),
                    "Ditambahkan ke Keranjang",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigateUp()

            }
        }
    }

    private fun checkAddonStock(
        addon: AddOn,
        onResult: (Boolean) -> Unit
    ) {

        Log.d(TAG, "checkAddonStock -> ${addon.name}")

        if (addon.bahanList.isEmpty()) {

            Log.d(
                TAG,
                "Addon ${addon.name} tidak memiliki bahan"
            )

            onResult(true)
            return
        }

        var checkedCount = 0
        var stockAvailable = true

        addon.bahanList.forEach { bahanItem ->

            bahanRepository.getBahanById(
                bahanItem.bahanId
            ) { bahan ->

                checkedCount++

                if (bahan == null) {

                    Log.e(
                        TAG,
                        "Bahan addon tidak ditemukan: ${bahanItem.bahanId}"
                    )

                    stockAvailable = false

                } else {

                    Log.d(
                        TAG,
                        "Addon=${addon.name} | Bahan=${bahan.nama} | Stok=${bahan.berat} | Dibutuhkan=${bahanItem.jumlah}"
                    )

                    if (bahan.berat < bahanItem.jumlah) {

                        Log.w(
                            TAG,
                            "Stok bahan addon tidak cukup"
                        )

                        stockAvailable = false
                    }
                }

                if (checkedCount == addon.bahanList.size) {

                    Log.d(
                        TAG,
                        "Hasil addon ${addon.name} = $stockAvailable"
                    )

                    onResult(stockAvailable)
                }
            }
        }
    }

    private fun checkStock(
        bahanList: List<BahanItem>
    ) {

        Log.d(
            TAG,
            "checkStock menu dijalankan"
        )

        if (bahanList.isEmpty()) {

            tvOutOfStock.visibility = View.GONE

            if (isEditMode) {
                btnOrder.visibility = View.GONE
                btnUpdateOrder.visibility = View.VISIBLE
            } else {
                btnOrder.visibility = View.VISIBLE
                btnUpdateOrder.visibility = View.GONE
            }

            return
        }

        var checkedCount = 0
        var stockAvailable = true

        bahanList.forEach { bahanItem ->

            bahanRepository.getBahanById(
                bahanItem.bahanId
            ) { bahan ->

                checkedCount++

                if (bahan == null) {

                    Log.e(
                        TAG,
                        "Bahan menu tidak ditemukan: ${bahanItem.bahanId}"
                    )

                    stockAvailable = false

                } else {

                    Log.d(
                        TAG,
                        "Bahan=${bahan.nama} | Stok=${bahan.berat} | Dibutuhkan=${bahanItem.jumlah}"
                    )

                    if (bahan.berat < bahanItem.jumlah) {

                        Log.w(
                            TAG,
                            "Stok tidak cukup untuk ${bahan.nama}"
                        )

                        stockAvailable = false
                    }
                }

                if (checkedCount == bahanList.size) {

                    Log.d(
                        TAG,
                        "Status stok menu = $stockAvailable"
                    )

                    if (stockAvailable) {

                        tvOutOfStock.visibility = View.GONE

                        if (isEditMode) {
                            btnOrder.visibility = View.GONE
                            btnUpdateOrder.visibility = View.VISIBLE
                        } else {
                            btnOrder.visibility = View.VISIBLE
                            btnUpdateOrder.visibility = View.GONE
                        }

                    } else {

                        btnOrder.visibility = View.GONE
                        btnUpdateOrder.visibility = View.GONE
                        tvOutOfStock.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun onAddonSelected(
        addon: AddOn,
        isChecked: Boolean
    ) {

        Log.d(
            TAG,
            "Addon dipilih -> ${addon.name} | checked=$isChecked"
        )

        if (isChecked) {

            selectedAddons.add(addon)

            selectedAddonPrice += addon.price

        } else {

            selectedAddons.remove(addon)

            selectedAddonPrice -= addon.price
        }

        Log.d(
            TAG,
            "Total harga addon = $selectedAddonPrice"
        )

        updateTotal()
    }

    private fun updateTotal() {

        val total =
            (basePrice + selectedAddonPrice) * quantity

        Log.d(
            TAG,
            "Update Total -> Base=$basePrice Addon=$selectedAddonPrice Qty=$quantity Total=$total"
        )

        tvTotal.text = "Rp. $total"
    }
}