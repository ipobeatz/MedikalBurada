package com.android.medikalburada.ui.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.medikalburada.R
import com.android.medikalburada.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileAdapter: ProfileAdapter
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserProfile()
        // Verileri oluştur
        val menuItems = listOf(
            "Hesap Bilgileri",
            "Şifre Değiştir",
        )

        // Adaptörü ve RecyclerView'i ayarla
        profileAdapter = ProfileAdapter(menuItems) { selectedItem ->
            onMenuItemClick(selectedItem)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = profileAdapter
        }

        // Çıkış yapma butonu
        binding.btnLogout.setOnClickListener {
            auth.signOut() // Firebase hesabından çıkış
            Toast.makeText(requireContext(), "Çıkış yapıldı!", Toast.LENGTH_SHORT).show()

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.profileFragment, inclusive = true) // Şu anki fragment yığından silinecek
                .build()

            findNavController().navigate(R.id.welcomeFragment, null, navOptions)

        }
    }

    private fun onMenuItemClick(item: String) {
        when (item) {
            "Hesap Bilgileri" -> {
                findNavController().navigate(R.id.action_profileFragment_to_personalInfoFragment)
            }
            "Şifre Değiştir" -> {
                showChangePasswordDialog()
            }
            else -> {
                Toast.makeText(requireContext(), "$item seçildi", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .setTitle("Şifre Değiştir")
            .setPositiveButton("Kaydet", null)
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            val currentPasswordInput = dialogView.findViewById<android.widget.EditText>(R.id.etCurrentPassword)
            val newPasswordInput = dialogView.findViewById<android.widget.EditText>(R.id.etNewPassword)
            val confirmPasswordInput = dialogView.findViewById<android.widget.EditText>(R.id.etConfirmNewPassword)

            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val currentPassword = currentPasswordInput.text.toString()
                val newPassword = newPasswordInput.text.toString()
                val confirmPassword = confirmPasswordInput.text.toString()

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Tüm alanları doldurunuz!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(requireContext(), "Yeni şifreler eşleşmiyor!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Şifre doğrulama ve değiştirme işlemi
                changePassword(currentPassword, newPassword) {
                    if (it) {
                        Toast.makeText(requireContext(), "Şifre başarıyla değiştirildi!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Şifre değiştirilemedi!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun changePassword(currentPassword: String, newPassword: String, callback: (Boolean) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (email == null || user == null) {
            callback(false)
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    callback(updateTask.isSuccessful)
                }
            } else {
                callback(false)
            }
        }
    }

    private fun setupUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val email = user.email ?: "Mail Bulunamadı"
            binding.mailName.text = email

            // Firestore'dan kullanıcı bilgilerini al
            FirebaseFirestore.getInstance().collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val firstName = document.getString("firstName") ?: "Ad Bulunamadı"
                        val lastName = document.getString("lastName") ?: "Soyad Bulunamadı"
                        val displayName = "$firstName $lastName"
                        binding.accountName.text = displayName

                        val initials = getInitialsFromName(displayName)
                        binding.profileInitials.text = initials
                    } else {
                        binding.accountName.text = "Ad Soyad Bulunamadı"
                        binding.profileInitials.text = "?"
                    }
                }
                .addOnFailureListener {
                    binding.accountName.text = "Ad Soyad Bulunamadı"
                    binding.profileInitials.text = "?"
                }
        }
    }

    private fun getInitialsFromName(name: String): String {
        val words = name.split(" ")
        return when (words.size) {
            0 -> "?"
            1 -> words[0].take(1).uppercase()
            else -> words[0].take(1).uppercase() + words[1].take(1).uppercase()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}