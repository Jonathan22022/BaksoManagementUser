package com.example.baksomanagement

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.baksomanagement.data.remote.FirebaseClient
import com.example.baksomanagement.data.repository.AuthRepository
import com.example.baksomanagement.data.repository.UserRepository
import com.example.baksomanagement.ui.HistoryFragment
import com.example.baksomanagement.ui.HomepageFragment
import com.example.baksomanagement.ui.SearchFragment
import com.example.baksomanagement.ui.SettingFragment
import com.example.baksomanagement.ui.aboutUs.AboutUsFragment
import com.example.baksomanagement.ui.favourite.FavouriteFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class HomepageActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val firestore = FirebaseClient.firestore
    private lateinit var imageViewProfile: ImageView
    private lateinit var tvUserName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navigationView = findViewById<NavigationView>(R.id.navigationDrawer)
        val headerView = navigationView.getHeaderView(0)
        imageViewProfile = headerView.findViewById(R.id.imageViewProfile)
        tvUserName = headerView.findViewById(R.id.tvUserName)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        // BOTTOM NAVIGATION
        val navController = supportFragmentManager
            .findFragmentById(R.id.home_fragment_container)
                as NavHostFragment
        val controller = navController.navController

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> controller.navigate(R.id.homepageFragment)
                R.id.menu_makanan -> controller.navigate(R.id.menu_makanan)
                R.id.menu_cart -> controller.navigate(R.id.cartFragment)
                R.id.menu_account -> controller.navigate(R.id.menu_account)
            }
            true
        }

        // DRAWER MENU
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> controller.navigate(R.id.homepageFragment)
                R.id.menu_search -> controller.navigate(R.id.menu_search)
                R.id.menu_history -> controller.navigate(R.id.menu_history)
                R.id.menu_favourite -> controller.navigate(R.id.menu_favourite)
                R.id.menu_about_us -> controller.navigate(R.id.menu_about_us)
                R.id.menu_setting -> controller.navigate(R.id.menu_setting)
                R.id.menu_logout -> {
                    authRepository.logout()
                    val intent =
                        Intent(this, MainActivity::class.java) // activity yg ada FirstPageFragment
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.close()
            true
        }
        loadUserData()
    }
    //Perbaiki lagi agar ketika udah login, malah muncul halaman firstpage di halaman homepage tapi jika pindah ke halaman lain
    //lalu pindah lagi ke halaman homepage, baru muncul tampilan halaman homepage

    private fun loadUserData() {
        userRepository.getCurrentUserData { user ->
            if (user != null) {
                tvUserName.text = user.nama
                val resId = resources.getIdentifier(
                    user.profilePicture,
                    "drawable",
                    packageName
                )

                if (resId != 0) {
                    imageViewProfile.setImageResource(resId)
                } else {
                    imageViewProfile.setImageResource(R.drawable.ic_account_)
                }

                Glide.with(this)
                    .load(user.profilePicture)
                    .into(imageViewProfile)
            }
        }
    }
}