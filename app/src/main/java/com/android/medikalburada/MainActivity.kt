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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding ile layout'u bağla
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val userRole = getUserRole()
        if (userRole == "admin") {
            binding.navAdminView.visibility = View.VISIBLE
            binding.navUserView.visibility = View.GONE
            binding.navAdminView.setupWithNavController(navController)
        } else {
            binding.navUserView.visibility = View.VISIBLE
            binding.navAdminView.visibility = View.GONE
            binding.navUserView.setupWithNavController(navController)
        }

        // Giriş ekranlarında Bottom Navigation'ı gizle
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.adminLoginFragment, R.id.welcomeFragment -> {
                    // Giriş ekranlarında Bottom Navigation'ı gizle
                    binding.navUserView.visibility = View.GONE
                    binding.navAdminView.visibility = View.GONE
                }
                else -> {
                    // Kullanıcı rolüne göre Bottom Navigation görünürlüğü ayarla
                    val userRole = getUserRole()
                    if (userRole == "admin") {
                        binding.navAdminView.visibility = View.VISIBLE
                        binding.navUserView.visibility = View.GONE
                        binding.navAdminView.setupWithNavController(navController)
                    } else {
                        binding.navUserView.visibility = View.VISIBLE
                        binding.navAdminView.visibility = View.GONE
                        binding.navUserView.setupWithNavController(navController)
                    }
                }
            }
        }
        }

    // Örnek kullanıcı rolü belirleme fonksiyonu
    private fun getUserRole(): String {
        // Burada kullanıcı rolü Firebase ya da başka bir yöntemle belirlenebilir.
        val authManager = AuthManager()
        val currentUser = authManager.getCurrentUser()
        return if (currentUser?.email == "admin@medikalburada.com") "admin" else "user"

    }
}