package com.android.medikalburada.ui.admin.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.medikalburada.ProductRepository
import com.android.medikalburada.databinding.FragmentAddProductBinding
import java.util.UUID

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val productRepository = ProductRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addProductButton.setOnClickListener {
            val name = binding.productNameInput.text.toString()
            val price = binding.productPriceInput.text.toString()
            val description = binding.productDescriptionInput.text.toString()
            val count = binding.productCountInput.text.toString().toIntOrNull() ?: 0
            val imageUrl = binding.productImageUrlInput.text.toString()

            if (name.isNotBlank() && price.isNotBlank() && description.isNotBlank() && imageUrl.isNotBlank()) {
                val productMap = mapOf(
                    "id" to UUID.randomUUID().toString(),
                    "name" to name,
                    "price" to price,
                    "description" to description,
                    "count" to count,
                    "imageUrl" to imageUrl
                )

                productRepository.addProduct(productMap) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Ürün başarıyla eklendi!", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    } else {
                        Toast.makeText(requireContext(), "Ürün eklenirken bir hata oluştu!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearInputFields() {
        binding.productNameInput.text.clear()
        binding.productPriceInput.text.clear()
        binding.productDescriptionInput.text.clear()
        binding.productCountInput.text.clear()
        binding.productImageUrlInput.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}