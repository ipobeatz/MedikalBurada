package com.android.medikalburada.ui.admin.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.medikalburada.databinding.FragmentPersonalInfoAdminBinding
import com.android.medikalburada.databinding.FragmentPersonalInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PersonalInfoAdminFragment : Fragment() {

    private var _binding: FragmentPersonalInfoAdminBinding? = null
    private val binding get() = _binding!!
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()

        // Kaydet butonu
        binding.btnSave.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(requireContext(), "Tüm alanları doldurunuz!", Toast.LENGTH_SHORT).show()
            } else {
                saveUserData(firstName, lastName)
            }
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        val userId = user?.uid ?: return

        // E-posta adresini sadece göster
        binding.etEmail.setText(user.email)

        // Firestore'dan diğer bilgileri yükle
        firestore.collection("adminCodes").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.etFirstName.setText(document.getString("firstName"))
                    binding.etLastName.setText(document.getString("lastName"))
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Kullanıcı bilgileri alınamadı!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserData(firstName: String, lastName: String) {
        val userId = auth.currentUser?.uid ?: return

        // Firestore'da kullanıcı verilerini güncelle
        firestore.collection("adminCodes").document(userId)
            .update(
                mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName
                )
            )
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Bilgiler kaydedildi!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Kullanıcı bilgileri güncellenemedi!", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}