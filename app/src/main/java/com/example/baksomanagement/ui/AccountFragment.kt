package com.example.baksomanagement.ui

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.baksomanagement.R
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val tvNama = view.findViewById<TextView>(R.id.tvNama)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            tvEmail.text = user.email
            tvNama.text = user.email?.substringBefore("@")
        }

        return view
    }
}