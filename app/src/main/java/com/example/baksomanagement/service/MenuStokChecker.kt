package com.example.baksomanagement.service

import com.example.baksomanagement.data.model.BahanBaku
import com.example.baksomanagement.data.model.KekuranganBahan
import com.example.baksomanagement.data.model.Menu

object MenuStokChecker {

    fun checkMenuStock(
        menu: Menu,
        bahanList: List<BahanBaku>
    ): List<KekuranganBahan> {

        val result = mutableListOf<KekuranganBahan>()

        menu.bahanList.forEach { kebutuhan ->

            val bahan =
                bahanList.find {
                    it.id == kebutuhan.bahanId
                }

            if (
                bahan != null &&
                bahan.berat < kebutuhan.jumlah
            ) {

                result.add(
                    KekuranganBahan(
                        bahanId = bahan.id,
                        namaBahan = bahan.nama,
                        kebutuhan = kebutuhan.jumlah,
                        stok = bahan.berat,
                        satuan = bahan.satuan,
                        kekurangan =
                            kebutuhan.jumlah - bahan.berat
                    )
                )
            }
        }

        return result
    }
}