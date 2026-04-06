package com.example.baksomanagement.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.baksomanagement.R

class CheckoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_checkout, container, false)

        val btnCheckout = view.findViewById<Button>(R.id.btnCheckout)

        btnCheckout.setOnClickListener {

            showConfirmationDialog()
        }

        return view
    }

    private fun showConfirmationDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Order")
            .setMessage("Apakah orderan sudah sesuai?")
            .setPositiveButton("Ya") { _, _ ->

                // PINDAH KE PAYMENT
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PaymentMethodFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .setNegativeButton("Cek lagi", null)
            .show()
    }
}