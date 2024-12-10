package com.android.medikalburada.ui.admin.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.android.medikalburada.AuthManager
import com.android.medikalburada.MainActivity
import com.android.medikalburada.R
import com.android.medikalburada.databinding.FragmentAdminLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class AdminLoginFragment : Fragment() {

    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!
    private val authManager = AuthManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdminLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val inviteCode = binding.etInviteCode.text.toString().trim()

            // E-posta ve admin kodunu kontrol et
            validateAdminCode(email, inviteCode) { isValid ->
                if (isValid) {
                    authManager.login(email, password) { success ->
                        if (success) {
                            Toast.makeText(context, "Admin girişi başarılı!", Toast.LENGTH_SHORT)
                                .show()
                            ((requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.nav_user_view)).menu.clear()
                            ((requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.nav_user_view)).inflateMenu(R.menu.bottom_nav_admin_menu)
                            findNavController().navigate(R.id.action_adminLoginFragment_to_productsFragment)

                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.adminLoginFragment, inclusive = true) // Şu anki fragment yığından silinecek
                                .build()

                            findNavController().navigate(R.id.productsFragment, null, navOptions)
                        } else {
                            Toast.makeText(context, "Giriş başarısız!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Geçersiz e-posta veya davet kodu!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun validateAdminCode(email: String, inputCode: String, onResult: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("adminCodes")
            .whereEqualTo("email", email) // E-postayı kontrol et
            .whereEqualTo("code", inputCode) // Admin kodunu kontrol et
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    android.util.Log.d("AdminLogin", "No matching document for email: $email and code: $inputCode")
                    onResult(false) // Eşleşme yok
                } else {
                    android.util.Log.d("AdminLogin", "Matching document found for email: $email and code: $inputCode")
                    onResult(true) // Eşleşme bulundu
                }
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("AdminLogin", "Error fetching documents", exception)
                onResult(false)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
