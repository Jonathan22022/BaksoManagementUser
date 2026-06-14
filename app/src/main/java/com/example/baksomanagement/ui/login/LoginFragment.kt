package com.example.baksomanagement.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.baksomanagement.HomepageActivity
import com.example.baksomanagement.R
import com.example.baksomanagement.ui.login.LoginViewModel
import com.example.baksomanagement.utils.SavedAccountManager
import com.example.baksomanagement.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private val TAG = "LoginFragment"

    private var currentEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate dipanggil")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView dipanggil")
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d(TAG, "onViewCreated dipanggil")

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            currentEmail = email

            Log.d(TAG, "Tombol Login ditekan")
            Log.d(TAG, "Email: $email")
            Log.d(TAG, "Password length: ${password.length}")

            viewModel.login(email, password)
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->

            Log.d(TAG, "Hasil login diterima: ${result.first}")

            if (result.first) {

                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener { token ->

                        Log.d(
                            "FCM_DEBUG",
                            "FCM TOKEN = $token"
                        )

                        val uid =
                            FirebaseAuth.getInstance()
                                .currentUser?.uid

                        Log.d(
                            "FCM_DEBUG",
                            "UID = $uid"
                        )

                        if (uid != null) {

                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .update(
                                    "fcmToken",
                                    token
                                )
                                .addOnSuccessListener {

                                    Log.d(
                                        "FCM_DEBUG",
                                        "Token berhasil disimpan ke Firestore"
                                    )
                                }
                                .addOnFailureListener {

                                    Log.e(
                                        "FCM_DEBUG",
                                        "Gagal simpan token",
                                        it
                                    )
                                }
                        }
                    }

                Log.i(TAG, "Login berhasil, membuka HomepageActivity")

                Toast.makeText(
                    requireContext(),
                    "Login Berhasil",
                    Toast.LENGTH_SHORT
                ).show()

                SavedAccountManager.saveAccount(
                    requireContext(),
                    currentEmail
                )

                val intent =
                    Intent(requireContext(), HomepageActivity::class.java)

                startActivity(intent)

                requireActivity().finish()

            } else {

                Log.e(TAG, "Login gagal: ${result.second}")

                Toast.makeText(
                    requireContext(),
                    result.second ?: "Login Gagal",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val tvForgot = view.findViewById<TextView>(R.id.tvForgotPassword)

        tvForgot.setOnClickListener {

            findNavController().navigate(
                R.id.action_loginFragment_to_lupaPasswordFragment
            )
        }

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {

            Log.d(TAG, "Tombol back ditekan")

            findNavController().navigate(
                R.id.action_loginFragment_to_firstPageFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView dipanggil")
    }
}