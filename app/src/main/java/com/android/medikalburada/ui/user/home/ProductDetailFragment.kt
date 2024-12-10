package com.android.medikalburada.ui.user.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.medikalburada.ProductRepository
import com.android.medikalburada.databinding.FragmentProductDetailBinding
import com.bumptech.glide.Glide

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs() // Ürün bilgisi almak için

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        // Ürün bilgilerini ekrana doldur
        binding.apply {
            productName.text = product.name
            productPrice.text = product.price
            productDescription.text = product.description
            productCount.text = "Stok: ${product.count}"

            Glide.with(productImage.context)
                .load(product.imageUrl)
                .into(productImage)

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            // Satın alma işlemi
            buyButton.setOnClickListener {
                // Eğer ürün zaten sepette varsa, sayısını artır.
                val existingProduct = ProductRepository.myCartList.find { it.id == product.id }
                if (existingProduct != null) {
                    ProductRepository.myCartList.remove(existingProduct)
                    ProductRepository.myCartList.add(
                        existingProduct.copy(count = existingProduct.count + 1)
                    )
                } else {
                    ProductRepository.myCartList.add(product.copy(count = 1))
                }

                Toast.makeText(
                    requireContext(),
                    "${product.name} sepete eklendi!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}