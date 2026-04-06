package com.example.baksomanagement.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baksomanagement.R

class PaymentConfirmationFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPay = view.findViewById<View>(R.id.btnPay)

        btnPay.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EnterPinFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}