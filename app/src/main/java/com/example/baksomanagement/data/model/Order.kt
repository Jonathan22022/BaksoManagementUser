package com.example.baksomanagement.data.model

//STRUKTUR DATABASE ORDER

//1. orders (header / transaksi utama)

//Menyimpan info order secara keseluruhan

//orders
//- id (PK)
//- user_id (optional, kalau ada login)
//- created_at
//- status (pending / paid / completed / canceled)
//- total_harga (optional, bisa dihitung juga)

//2. order_items (isi dari order)

//Setiap menu yang dipesan

//order_items
//- id (PK)
//- order_id (FK → orders.id)
//- menu_id (FK → menus.id)
//- quantity
//- harga_satuan (snapshot, penting!)
//- catatan (keterangan tambahan)

//3. order_item_addons (add-on per item)

//Add-on yang dipilih untuk setiap menu dalam order

//order_item_addons
//- id (PK)
//- order_item_id (FK → order_items.id)
//- addon_id (FK → addons.id)
//- harga_addon (snapshot, penting!)

//orders: {
//    orderId1: {
//        userID: "nigger",
//        created_at: "...",
//        status: "pending",
//        items: {
//        item1: {
//        menu_id: "1",
//        nama: "Bakso",
//        harga: 10000,
//        quantity: 2,
//        catatan: "pedas",
//        addons: [
//        { id: "1", nama: "Keju", harga: 3000 },
//        { id: "2", nama: "Sambal", harga: 2000 }
//        ]
//    }
//    }
//    }
//}

data class Order(
    val id:String = "",
    val userID:String = "",
    val createdAt:Long = System.currentTimeMillis(),
    val total:Int = 0,
    val status:String = "pending"
)
