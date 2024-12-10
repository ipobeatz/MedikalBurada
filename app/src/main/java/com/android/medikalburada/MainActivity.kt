package com.android.medikalburada

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.medikalburada.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding ile layout'u bağla
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation Controller başlat
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            navController = navHostFragment.navController

            // Kullanıcı durumu kontrol etmeden önce navController başlatıldığından emin olun
        val userRole = getUserRole()
        //checkUserLoginStatus()

        binding.navUserView.setupWithNavController(navController)

            // Giriş ekranlarında Bottom Navigation'ı gizle
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val userRole_ = getUserRole()
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.adminLoginFragment, R.id.welcomeFragment -> {
                    binding.navUserView.visibility = View.GONE
                }
                else -> {
                    binding.navUserView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getUserRole(): String {
        val authManager = AuthManager()
        val currentUser = authManager.getCurrentUser()
        return if (currentUser?.email == "admin@medikalburada.com") "admin" else "user"
    }

    private fun checkUserLoginStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val role = document.getString("role")
                        when (role) {
                            "admin" -> {
                                navController.navigate(R.id.productsFragment) // Admin ekranına yönlendir
                            }
                            "user" -> {
                                navController.navigate(R.id.homeFragment) // Kullanıcı ekranına yönlendir
                            }
                            else -> {
                                navController.navigate(R.id.welcomeFragment) // Welcome ekranına yönlendir
                            }
                        }
                    } else {
                        navController.navigate(R.id.welcomeFragment)
                    }
                }
                .addOnFailureListener {
                    navController.navigate(R.id.welcomeFragment)
                }
        } else {
            navController.navigate(R.id.welcomeFragment)
        }
    }
}

