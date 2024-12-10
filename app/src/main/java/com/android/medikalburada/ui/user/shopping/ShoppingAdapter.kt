package com.android.medikalburada.ui.user.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.medikalburada.R
import com.android.medikalburada.ui.user.home.ProductModel
import com.bumptech.glide.Glide

class ShoppingAdapter(
    private val items: List<ProductModel>,
    private val onItemCountChanged: (List<ProductModel>) -> Unit // Sepet güncellendiğinde çağrılacak
) : RecyclerView.Adapter<ShoppingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecrease)
        val tvItemCount: TextView = itemView.findViewById(R.id.tvItemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shopping_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = items[position]

        // Ürün bilgilerini bağla
        holder.productName.text = product.name
        holder.productPrice.text = "₺${product.price.replace("₺", "").toDouble() * product.count}"
        holder.productDescription.text = product.description
        holder.tvItemCount.text = product.count.toString()

        // Ürün görselini yükle
        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .into(holder.productImage)

        // Ürün miktarını artırma
        holder.btnIncrease.setOnClickListener {
            product.count += 1
            notifyItemChanged(position)
            onItemCountChanged(items) // Sepet güncellendiğinde dışarıya bildir
        }

        // Ürün miktarını azaltma
        holder.btnDecrease.setOnClickListener {
            if (product.count > 1) {
                product.count -= 1
                notifyItemChanged(position)
                onItemCountChanged(items) // Sepet güncellendiğinde dışarıya bildir
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
