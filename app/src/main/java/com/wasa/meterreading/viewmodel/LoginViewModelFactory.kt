package com.wasa.meterreading.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wasa.meterreading.data.api.ApiHelper
import com.wasa.meterreading.data.repository.MainRepository

class LoginViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(MainRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}