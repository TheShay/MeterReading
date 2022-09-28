package com.wasa.meterreading.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.wasa.meterreading.data.repository.MainRepository
import com.wasa.meterreading.utils.Resource
import kotlinx.coroutines.*

class LoginViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun login(userName: String, password: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getLogin(userName, password)))
        } catch (e: Exception) {
            val exception = "[Exception in DetectorViewModel:login] [${e.localizedMessage}]".trimIndent()
            Log.d("LoginViewModel", exception)
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }

    override fun onCleared() {
        try {
            super.onCleared()

            Log.i("DetectorViewModel", "DetectorViewModel destroyed!")
        } catch (e: Exception) {
            val exception = "[Exception in DetectorViewModel:onCleared] [${e.localizedMessage}]".trimIndent()
            Log.d("LoginViewModel", exception)
        }
    }
}