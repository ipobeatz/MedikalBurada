package com.android.medikalburada.ui.admin.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.medikalburada.databinding.ItemManageProductBinding
import com.android.medikalburada.ui.user.home.ProductModel
import com.bumptech.glide.Glide

class ManageProductsAdapter(
    private var productList: List<ProductModel>,
    private val onUpdateProduct: (ProductModel) -> Unit,
    private val onDeleteProduct: (String) -> Unit
) : RecyclerView.Adapter<ManageProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemManageProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemManageProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.apply {
            productName.setText(product.name)
            productPrice.setText(product.price)
            productCount.text = product.count.toString()

            // Ürün resmini yükleme
            Glide.with(productImage.context)
                .load(product.imageUrl)
                .into(productImage)

            // Stok azaltma
            btnDecreaseCount.setOnClickListener {
                if (product.count > 0) {
                    product.count--
                    productCount.text = product.count.toString()
                    onUpdateProduct(product)
                }
            }

            // Stok artırma
            btnIncreaseCount.setOnClickListener {
                product.count++
                productCount.text = product.count.toString()
                onUpdateProduct(product)
            }

            // Ürün silme
            btnDeleteProduct.setOnClickListener {
                onDeleteProduct(product.id)
            }
        }
    }
    fun updateData(newProductList: List<ProductModel>) {
        productList = newProductList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = productList.size
}

