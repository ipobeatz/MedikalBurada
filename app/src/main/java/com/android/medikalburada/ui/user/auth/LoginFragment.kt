package com.android.medikalburada.ui.user.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.android.medikalburada.AuthManager
import com.android.medikalburada.MainActivity
import com.android.medikalburada.R
import com.android.medikalburada.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class LoginFragment constructor() : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveUserType("admin")


        saveUserType("user")

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        auth = FirebaseAuth.getInstance()


        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            binding.tvRegisterNow.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }


            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT)
                        .show()

                    ((requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.nav_user_view)).menu.clear()
                    ((requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.nav_user_view)).inflateMenu(R.menu.bottom_nav_user_menu)

                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, inclusive = true) // Şu anki fragment yığından silinecek
                        .build()

                    findNavController().navigate(R.id.homeFragment, null, navOptions)

                } else {

                    Toast.makeText(
                        requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun saveUserType(userType: String) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        val editor = sharedPreferences.edit()
        editor.putString("userType", userType) // "admin" veya "user" olarak kaydet
        editor.apply()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getUserRole(): String {
        val authManager = AuthManager()
        val currentUser = authManager.getCurrentUser()
        return if (currentUser?.email == "admin@medikalburada.com") "admin" else "user"
    }
}