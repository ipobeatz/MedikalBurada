package com.android.medikalburada.ui.user.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.medikalburada.ProductRepository
import com.android.medikalburada.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private val productRepository = ProductRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        productRepository.getAllProducts { productList ->
            if (productList != null) {
                // Ürünler başarıyla alındıktan sonra RecyclerView'a aktar
                setupRecyclerView(productList)
            } else {
                Toast.makeText(context, "Ürünler alınamadı!", Toast.LENGTH_SHORT).show()
            }
        }

        // Test amaçlı ürün oluşturma

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView(productList: List<ProductModel>) {
        val adapter = ProductAdapter(productList) { product ->
            // Ürün detaylarına geçiş
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product)
            findNavController().navigate(action)
        }

        binding.homeFragmentRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.homeFragmentRecyclerView.adapter = adapter
    }


    }

