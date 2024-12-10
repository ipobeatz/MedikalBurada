package com.android.medikalburada.ui.user.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.medikalburada.ProductRepository
import com.android.medikalburada.databinding.FragmentShoppingBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class ShoppingFragment : Fragment() {

    private var _binding: FragmentShoppingBinding? = null
    private val binding get() = _binding!!
    private lateinit var shoppingAdapter: ShoppingAdapter
    private val productRepository = ProductRepository()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadProducts()
        calculateTotalPrice()

        // Sipariş Onayla düğmesi
        binding.btnConfirmOrder.setOnClickListener {
            confirmOrder()
        }
    }

    private fun setupRecyclerView() {
        shoppingAdapter = ShoppingAdapter(emptyList()) { productList ->
            calculateTotalPrice()
        }
        binding.shoppingFragmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shoppingAdapter
        }
    }

    private fun loadProducts() {
        val cartItems = ProductRepository.myCartList

        if (cartItems.isEmpty()) {
            binding.emptyCartTextView.visibility = View.VISIBLE
            binding.shoppingFragmentRecyclerView.visibility = View.GONE
            binding.btnConfirmOrder.visibility = View.GONE
            binding.totalPriceTextView.visibility = View.GONE
        } else {
            binding.emptyCartTextView.visibility = View.GONE
            binding.shoppingFragmentRecyclerView.visibility = View.VISIBLE
            binding.btnConfirmOrder.visibility = View.VISIBLE
            binding.totalPriceTextView.visibility = View.VISIBLE
        }

        shoppingAdapter = ShoppingAdapter(cartItems) { productList ->
            calculateTotalPrice()
        }
        binding.shoppingFragmentRecyclerView.adapter = shoppingAdapter
    }

    private fun calculateTotalPrice() {
        if (ProductRepository.myCartList.isEmpty()) {
            binding.totalPriceTextView.text = "Toplam Fiyat: ₺0"
            return
        }

        val totalPrice = ProductRepository.myCartList.sumOf {
            it.price.replace("₺", "").toDouble() * it.count
        }
        binding.totalPriceTextView.text = "Toplam Fiyat: ₺$totalPrice"
    }

    private fun confirmOrder() {
        val cartItems = ProductRepository.myCartList
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Sepet boş!", Toast.LENGTH_SHORT).show()
            return
        }

        // Siparişleri Firebase'e kaydet
        cartItems.forEach { product ->
            val orderData = hashMapOf(
                "id" to product.id,
                "name" to product.name,
                "description" to product.description,
                "price" to product.price,
                "count" to product.count,
                "imageUrl" to product.imageUrl
            )
            firestore.collection("orders").document(product.id).set(orderData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Sipariş başarıyla oluşturuldu!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Sipariş oluşturulamadı!", Toast.LENGTH_SHORT).show()
                }
        }

        // Sepeti temizle
        ProductRepository.myCartList.clear()
        shoppingAdapter.notifyDataSetChanged()
        calculateTotalPrice()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}