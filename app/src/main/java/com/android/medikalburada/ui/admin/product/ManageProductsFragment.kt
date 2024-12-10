package com.android.medikalburada.ui.admin.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.medikalburada.ProductRepository
import com.android.medikalburada.databinding.FragmentManageProductsBinding

class ManageProductsFragment : Fragment() {

    private var _binding: FragmentManageProductsBinding? = null
    private val binding get() = _binding!!
    private val productRepository = ProductRepository()
    private lateinit var adapter: ManageProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productRepository.getAllProducts { productList ->
            if (productList != null) {
                adapter = ManageProductsAdapter(
                    productList,
                    onUpdateProduct = { updatedProduct ->
                        productRepository.updateProduct(updatedProduct)
                        adapter.notifyDataSetChanged() // Listeyi güncelle
                    },
                    onDeleteProduct = { productId ->
                        productRepository.deleteProductById(productId) { success ->
                            if (success) {
                                // Ürünü silindikten sonra listeyi güncelle
                                refreshProducts()
                            }
                        }
                    }
                )
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.recyclerView.adapter = adapter
            }
        }
        }

    private fun refreshProducts() {
        productRepository.getAllProducts { productList ->
            if (productList != null) {
                adapter.updateData(productList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}