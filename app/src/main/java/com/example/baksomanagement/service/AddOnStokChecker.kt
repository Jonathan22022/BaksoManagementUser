package com.example.baksomanagement.service

import com.example.baksomanagement.data.model.AddOn
import com.example.baksomanagement.data.model.BahanBaku
import com.example.baksomanagement.data.model.KekuranganBahan

object AddOnStokChecker {

    fun checkAddOnStock(
        addon: AddOn,
        bahanList: List<BahanBaku>
    ): List<KekuranganBahan> {

        val result = mutableListOf<KekuranganBahan>()

        addon.bahanList.forEach { kebutuhan ->

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