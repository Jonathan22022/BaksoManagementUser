package com.example.baksomanagement.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.baksomanagement.R
import com.google.firebase.firestore.FirebaseFirestore
import android.content.ClipData
import android.content.ClipboardManager

class PaymentDialogFragment(
    private val orderId:String,
    private val total:Int,
    /*
    private val bank:String,
    private val vaNumber:String
     */
): DialogFragment() {
    private lateinit var tvStatus: TextView
    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        val view =
            layoutInflater.inflate(
                R.layout.dialog_payment,
                null
            )

        val tvTotal =
            view.findViewById<TextView>(
                R.id.tvTotal
            )

        val tvBank =
            view.findViewById<TextView>(R.id.tvBank)

        val tvVA =
            view.findViewById<TextView>(R.id.tvVA)

        val btnCopyVA =
            view.findViewById<Button>(
                R.id.btnCopyVA
            )

        tvTotal.text = "Rp $total"
        tvStatus = view.findViewById(R.id.tvStatus)
        /*tvBank.text = bank.uppercase()
        tvVA.text = vaNumber*/
        btnCopyVA.setOnClickListener {

            val clipboard =
                requireContext().getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager

            /*
            val clip =
                ClipData.newPlainText(
                    "Virtual Account",
                    vaNumber
                )

            clipboard.setPrimaryClip(clip)
            */

            Toast.makeText(
                requireContext(),
                "Nomor Virtual Account berhasil disalin",
                Toast.LENGTH_SHORT
            ).show()
        }
        observePayment()

        return AlertDialog.Builder(requireContext())

            .setView(view)

            .create()

    }

    private fun observePayment() {

        FirebaseFirestore.getInstance()
            .collection("orders")
            .document(orderId)
            .addSnapshotListener { snapshot, _ ->

                if (snapshot == null) return@addSnapshotListener

                val status =
                    snapshot.getString("paymentStatus")
                        ?: "waiting"

                when(status){

                    "waiting"->{
                        tvStatus.text =
                            "Menunggu pembayaran..."
                    }

                    "paid"->{

                        tvStatus.text =
                            "Pembayaran berhasil ✓"

                        Toast.makeText(
                            requireContext(),
                            "Pembayaran berhasil",
                            Toast.LENGTH_SHORT
                        ).show()

                        dismiss()
                    }

                    "expire"->{
                        tvStatus.text =
                            "QRIS telah kedaluwarsa"
                    }

                    "cancel"->{
                        tvStatus.text =
                            "Pembayaran dibatalkan"
                    }
                }

            }

    }
}