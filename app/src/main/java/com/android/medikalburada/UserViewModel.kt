package com.android.medikalburada

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    fun fetchUserRole(uid: String) {
        userRepository.getUserRole(uid) { user ->
            _currentUser.postValue(user)
        }
    }
}