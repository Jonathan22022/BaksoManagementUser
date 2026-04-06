package com.example.baksomanagement.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.baksomanagement.R

class EnterPinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_pin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnVerify = view.findViewById<View>(R.id.btnVerify)

        btnVerify.setOnClickListener {

            android.widget.Toast.makeText(
                requireContext(),
                "Pembayaran berhasil ✔️",
                android.widget.Toast.LENGTH_SHORT
            ).show()

            // nanti bisa lanjut ke halaman sukses
        }
    }
}