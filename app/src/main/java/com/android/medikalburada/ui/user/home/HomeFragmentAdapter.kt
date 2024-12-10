package com.android.medikalburada.ui.user.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.medikalburada.R
import com.bumptech.glide.Glide

class ProductAdapter(
    private val productList: List<ProductModel>,
    private val onAddToCart: (ProductModel) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productCount: TextView = itemView.findViewById(R.id.productCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.productPrice.text = product.price
        holder.productDescription.text = product.description
        holder.productCount.text = "Stok: ${product.count}"

        holder.itemView.setOnClickListener {
            onAddToCart(product)
        }

        Glide.with(holder.productImage.context)
            .load(product.imageUrl)
            .into(holder.productImage)
    }

    override fun getItemCount(): Int = productList.size
}