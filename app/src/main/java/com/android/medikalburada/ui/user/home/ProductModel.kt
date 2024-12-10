package com.android.medikalburada.ui.user.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductModel(
    val id: String,
    val imageUrl: String,
    val name: String,
    val price: String,
    val description: String,
    var count: Int
) : Parcelable
