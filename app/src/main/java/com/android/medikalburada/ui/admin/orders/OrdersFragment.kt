package com.android.medikalburada.ui.admin.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.medikalburada.ProductRepository
import com.android.medikalburada.databinding.FragmentOrdersBinding
import com.android.medikalburada.ui.user.home.ProductModel
import com.google.firebase.firestore.FirebaseFirestore

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val productRepository = ProductRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadOrders()
    }

    private fun loadOrders() {
        firestore.collection("orders").get()
            .addOnSuccessListener { querySnapshot ->
                val orders = querySnapshot.documents.mapNotNull { document ->
                    ProductModel(
                        id = document.getString("id") ?: "",
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        price = document.getString("price") ?: "",
                        count = (document.getLong("count") ?: 1).toInt(),
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                }
                if (orders.isEmpty()) {
                    showEmptyMessage(true)
                } else {
                    showEmptyMessage(false)
                    setupRecyclerView(orders)
                }
            }
            .addOnFailureListener {
                showEmptyMessage(true) // Hata durumunda boş mesaj göster
            }
    }

    private fun setupRecyclerView(orderList: List<ProductModel>) {
        val adapter = OrdersAdapter(orderList) { selectedOrder ->
            productRepository.getProductById(selectedOrder.id) { product ->
                productRepository.updateProduct(selectedOrder.apply {
                    val newCountValue = (product?.count ?: 0) - (selectedOrder.count)
                    count = if (newCountValue < 0) 0 else newCountValue
                    productRepository.deleteOrderList(selectedOrder) {
                        refreshOrderList()
                    }
                })
            }
        }
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ordersRecyclerView.adapter = adapter
    }

    private fun refreshOrderList() {
        loadOrders()
    }

    private fun showEmptyMessage(show: Boolean) {
        if (show) {
            binding.ordersRecyclerView.visibility = View.GONE
            binding.emptyMessageTextView.visibility = View.VISIBLE
        } else {
            binding.ordersRecyclerView.visibility = View.VISIBLE
            binding.emptyMessageTextView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}