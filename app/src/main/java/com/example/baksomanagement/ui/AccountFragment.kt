package com.example.baksomanagement.ui

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.baksomanagement.R
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseClient.firestore

    private lateinit var imgProfile: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAccountAge: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_account, container, false)

        imgProfile = view.findViewById(R.id.imgProfile)
        tvNama = view.findViewById(R.id.tvNama)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvAccountAge = view.findViewById(R.id.tvAccountAge)

        loadUserData()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val nama = document.getString("nama")
                    val email = document.getString("email")
                    val phone = document.getString("noTelp")
                    val imageUrl = document.getString("profilePicture")
                    val createdAt = document.getLong("createdAt") ?: 0L

                    tvNama.text = nama
                    tvEmail.text = email
                    tvPhone.text = phone

                    // load foto profil
                    if (!imageUrl.isNullOrEmpty()) {

                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_account_)
                            .into(imgProfile)
                    }

                    // hitung usia akun
                    val now = System.currentTimeMillis()
                    val diff = now - createdAt

                    val days = diff / (1000 * 60 * 60 * 24)
                    val months = days / 30
                    val years = days / 365

                    val ageText = when {
                        years > 0 -> "$years tahun"
                        months > 0 -> "$months bulan"
                        else -> "$days hari"
                    }

                    tvAccountAge.text = ageText

                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Gagal mengambil data user",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }
}
