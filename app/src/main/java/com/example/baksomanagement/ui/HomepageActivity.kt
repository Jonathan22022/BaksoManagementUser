package com.example.baksomanagement

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.baksomanagement.data.repository.AuthRepository
import com.example.baksomanagement.ui.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.auth.FirebaseAuth

class HomepageActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseClient.firestore
    private val authRepository = AuthRepository()

    private fun loadHeaderUserData(imgProfile: ImageView, tvUserName: TextView) {

        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val nama = document.getString("nama")
                    val imageUrl = document.getString("profilePicture")

                    tvUserName.text = nama ?: "User"

                    if (!imageUrl.isNullOrEmpty()) {

                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_account_)
                            .circleCrop()
                            .into(imgProfile)

                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navigationView = findViewById<NavigationView>(R.id.navigationDrawer)
        val headerView = navigationView.getHeaderView(0)
        val imgProfile = headerView.findViewById<ImageView>(R.id.imageViewProfile)
        val tvUserName = headerView.findViewById<TextView>(R.id.tvUserName)
        loadHeaderUserData(imgProfile, tvUserName)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        // default fragment
        if (savedInstanceState == null) {
            loadFragment(HomepageFragment())
        }

        // BOTTOM NAVIGATION
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> loadFragment(HomepageFragment())
                R.id.menu_makanan -> loadFragment(MenuFragment())
                R.id.menu_cart -> loadFragment(CartFragment())
                R.id.menu_account -> loadFragment(AccountFragment())
            }
            true
        }

        // DRAWER MENU
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_dashboard -> loadFragment(HomepageFragment())
                R.id.menu_search -> loadFragment(SearchFragment())
                R.id.menu_history -> loadFragment(HistoryFragment())
                R.id.menu_favourite -> loadFragment(FavouriteFragment())
                R.id.menu_about_us -> loadFragment(AboutUsFragment())
                R.id.menu_setting -> loadFragment(SettingFragment())
                R.id.menu_logout -> {
                    authRepository.logout()
                    val intent = Intent(this, MainActivity::class.java) // activity yg ada FirstPageFragment
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.close()
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container, fragment)
            .commit()
    }

}