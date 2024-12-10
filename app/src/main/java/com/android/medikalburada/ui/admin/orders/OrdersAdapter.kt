package com.android.medikalburada.ui.admin.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.medikalburada.databinding.OrderRowBinding
import com.android.medikalburada.databinding.ShoppingRowBinding
import com.android.medikalburada.ui.user.home.ProductModel

class OrdersAdapter(
    private val orderList: List<ProductModel>,
    private val onOrderClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(val binding: OrderRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val binding = OrderRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = orderList[position]
        holder.binding.apply {
            productName.text = order.name
            productPrice.text = order.price
            productDescription.text = order.description
            tvItemCount.text = "${order.count} Adet"

            // Tıklama işlemi
            orderApproveButton.setOnClickListener {
                onOrderClick(order)
            }
        }
    }

    override fun getItemCount(): Int = orderList.size
}