package com.android.medikalburada.ui.admin.product

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentProductsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2 // 2 Tab: Ürünleri Listele ve Ürün Ekle
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ManageProductsFragment() // Ürünleri düzenlemek için
            1 -> AddProductFragment() // Yeni ürün eklemek için
            else -> throw IllegalStateException("Invalid position")
        }
    }
}