package com.example.baksomanagement.ui

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.baksomanagement.R
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment() {

    private var imageUri: Uri? = null
    private var imgProfile: ImageView? = null

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseClient.firestore

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                imgProfile?.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<View>(R.id.btnAccount).setOnClickListener {
            showEditProfileDialog()
        }

        view.findViewById<View>(R.id.btnNotification).setOnClickListener {
            Toast.makeText(requireContext(), "Notification Setting", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnLanguage).setOnClickListener {
            Toast.makeText(requireContext(), "Language Setting", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnBackground).setOnClickListener {
            Toast.makeText(requireContext(), "Background Setting", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnChangeAccount).setOnClickListener {
            showChangeAccountDialog()
        }

        view.findViewById<View>(R.id.btnDeleteAccount).setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun deleteAccount() {

        val user = auth.currentUser ?: return
        val uid = user.uid

        firestore.collection("users")
            .document(uid)
            .delete()
            .addOnSuccessListener {

                user.delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            requireContext(),
                            "Akun berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()

                        goToFirstPage()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            requireContext(),
                            "Gagal menghapus akun",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }

    private fun goToFirstPage() {
        findNavController().navigate(R.id.firstPageFragment)
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Akun")
            .setMessage("Jika akun dihapus maka semua data akan hilang. Apakah Anda yakin?")
            .setPositiveButton("Ya") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showChangeAccountDialog() {
        Toast.makeText(requireContext(), "Change Account", Toast.LENGTH_SHORT).show()
    }

    private fun showEditProfileDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)

        imgProfile = dialogView.findViewById(R.id.imgProfileEdit)
        val etNama = dialogView.findViewById<EditText>(R.id.etNama)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        val uid = auth.currentUser?.uid ?: return

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.show()

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->

                etNama.setText(doc.getString("nama"))
                etEmail.setText(doc.getString("email"))
                etPhone.setText(doc.getString("noTelp"))

                val imageUrl = doc.getString("profilePicture")

                if (!imageUrl.isNullOrEmpty()) {

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_account_)
                        .circleCrop()
                        .into(imgProfile!!)
                }
            }

        imgProfile!!.setOnClickListener {

            val options = arrayOf("Galeri")

            AlertDialog.Builder(requireContext())
                .setTitle("Pilih Foto")
                .setItems(options) { _, _ ->
                    pickImageLauncher.launch("image/*")
                }
                .show()
        }

        btnSave.setOnClickListener {

            val nama = etNama.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()

            if (imageUri != null) {

                uploadProfileImage(imageUri!!) { imageUrl ->

                    updateUser(uid, nama, email, phone, imageUrl)

                    dialog.dismiss()
                }

            } else {

                updateUser(uid, nama, email, phone, null)

                dialog.dismiss()
            }
        }
    }

    private fun uploadProfileImage(uri: Uri, onComplete: (String) -> Unit) {

        MediaManager.get()
            .upload(uri)
            .option("folder", "profile_images")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {

                    val imageUrl = resultData?.get("secure_url").toString()

                    onComplete(imageUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {

                    Toast.makeText(
                        requireContext(),
                        "Upload gagal",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }

    private fun updateUser(
        uid: String,
        nama: String,
        email: String,
        phone: String,
        imageUrl: String?
    ) {

        val updateMap = mutableMapOf<String, Any>(
            "nama" to nama,
            "email" to email,
            "noTelp" to phone,
            "updatedAt" to System.currentTimeMillis()
        )

        imageUrl?.let {
            updateMap["profilePicture"] = it
        }

        firestore.collection("users")
            .document(uid)
            .update(updateMap)
            .addOnSuccessListener {

                auth.currentUser?.updateEmail(email)

                Toast.makeText(
                    requireContext(),
                    "Profile berhasil diupdate",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}