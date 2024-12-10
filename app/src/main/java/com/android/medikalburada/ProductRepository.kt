package com.android.medikalburada

import com.android.medikalburada.ui.user.home.ProductModel
import com.google.firebase.firestore.FirebaseFirestore

class ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getProductById(id: String, onComplete: (ProductModel?) -> Unit) {
        firestore.collection("products").document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val productId = document.get("id").toString() // ID'yi açıkça String'e dönüştürüyoruz
                    val name = document.getString("name") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val price = document.getString("price") ?: ""
                    val description = document.getString("description") ?: ""
                    val count = (document.getLong("count") ?: 0).toInt()

                    val product = ProductModel(
                        id = productId,
                        name = name,
                        imageUrl = imageUrl,
                        price = price,
                        description = description,
                        count = count
                    )
                    onComplete(product)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun getAllProducts(onComplete: (List<ProductModel>?) -> Unit) {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val productList = querySnapshot.documents.mapNotNull { document ->
                    val id = document.get("id").toString() // ID'yi açıkça String'e dönüştürüyoruz
                    val name = document.getString("name") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val price = document.getString("price") ?: ""
                    val description = document.getString("description") ?: ""
                    val count = (document.getLong("count") ?: 0).toInt()

                    if (id.isNotEmpty() && name.isNotEmpty()) {
                        ProductModel(
                            id = id,
                            name = name,
                            imageUrl = imageUrl,
                            price = price,
                            description = description,
                            count = count
                        )
                    } else {
                        null
                    }
                }
                onComplete(productList)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }



    fun deleteProductById(productId: String, onComplete: (Boolean) -> Unit) {
        firestore.collection("products").document(productId)
            .delete()
            .addOnSuccessListener {
                onComplete(true) // Başarılı
            }
            .addOnFailureListener {
                onComplete(false) // Hata
            }
    }

    fun updateProduct(product: ProductModel) {
        val productMap = mapOf(
            "id" to product.id,
            "imageUrl" to product.imageUrl,
            "name" to product.name,
            "price" to product.price,
            "description" to product.description,
            "count" to product.count
        )

        firestore.collection("products").document(product.id)
            .set(productMap)
            .addOnSuccessListener {
                // Ürün başarıyla güncellendi
            }
            .addOnFailureListener {
                // Güncelleme sırasında hata oluştu
            }
    }

    fun updateOrDeleteProduct(id: String, onComplete: (Boolean) -> Unit) {
        val productRef = firestore.collection("products").document(id)

        productRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val product = ProductModel(
                        id = document.data?.get("id")!! as String,
                        name = document.data?.get("name")!! as String,
                        imageUrl = document.data?.get("imageUrl")!! as String,
                        price = document.data?.get("price")!! as String,
                        description = document.data?.get("description")!! as String,
                        count = document.data?.get("count") as? Int ?: 1,
                    )
                    product.let {
                        if (it.count > 1) {
                            // Count 1'den büyükse azalt
                            productRef.update("count", it.count - 1)
                                .addOnSuccessListener {
                                    onComplete(true)
                                }
                                .addOnFailureListener {
                                    onComplete(false)
                                }
                        } else {
                            // Count 1 ise belgeden sil
                            productRef.delete()
                                .addOnSuccessListener {
                                    onComplete(true)
                                }
                                .addOnFailureListener {
                                    onComplete(false)
                                }
                        }
                    } ?: onComplete(false)
                } else {
                    onComplete(false) // Belge bulunamadı
                }
            }
            .addOnFailureListener {
                onComplete(false) // Hata oluştu
            }
    }


    // Sipariş verilmiş ürünler datasını alma
    fun getAllOrderedProducts(onComplete: (List<ProductModel>?) -> Unit) {
        firestore.collection("orders")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val productList = querySnapshot.documents.mapNotNull { document ->
                    ProductModel(
                        id = document.data?.get("id")!! as String,
                        name = document.data?.get("name")!! as String,
                        imageUrl = document.data?.get("imageUrl")!! as String,
                        price = document.data?.get("price")!! as String,
                        description = document.data?.get("description")!! as String,
                        count = document.data?.get("count")!! as Int,
                    )
                }
                onComplete(productList)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun generateProducts() {
        val productMap = mapOf(
            "id" to "12",
            "imageUrl" to "https://via.placeholder.com/200",
            "name" to "Ürün 2",
            "price" to "₺2300",
            "count" to 3,
            "description" to "Bu ürün hakkında açıklama3." // Varsayılan olarak "user" rolü
        )

        firestore.collection("products").document(productMap["id"]!!.toString()).set(productMap)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    private fun generateOrderProducts() {
        val productMap = mapOf(
            "id" to "3",
            "imageUrl" to "https://via.placeholder.com/200",
            "name" to "Ürün 3",
            "price" to "₺300",
            "description" to "Bu ürün hakkında açıklama3." // Varsayılan olarak "user" rolü
        )

        firestore.collection("orders").document(productMap["id"]!!).set(productMap)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    fun deleteOrderList(product: ProductModel, onComplete: (Boolean?) -> Unit) {
        firestore.collection("orders").document(product.id)
            .delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete (false)
            }
    }

    fun addProduct(product: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        val id = product["id"] as? String ?: System.currentTimeMillis().toString() // Güvenli dönüşüm
        firestore.collection("products").document(id).set(product)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    companion object {
        var myCartList: ArrayList<ProductModel> = arrayListOf()
    }
}
