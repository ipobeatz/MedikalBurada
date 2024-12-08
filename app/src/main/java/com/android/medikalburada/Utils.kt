package com.android.medikalburada

object Utils {
    fun isAdmin(role: String): Boolean {
        return role == Constants.ROLE_ADMIN
    }

    fun isUser(role: String): Boolean {
        return role == Constants.ROLE_USER
    }
}