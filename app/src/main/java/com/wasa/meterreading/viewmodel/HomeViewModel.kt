package com.wasa.meterreading.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.wasa.meterreading.data.repository.MainRepository
import com.wasa.meterreading.utils.Resource
import kotlinx.coroutines.*

class HomeViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun logout(affiliateID: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getLogout()))
        } catch (e: Exception) {
            val exception = "[Exception in DetectorViewModel:logout] [${e.localizedMessage}]".trimIndent()
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }

    fun getCustomers(consumerCode: String, ddrId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getCustomers(consumerCode, ddrId)))
        } catch (e: Exception) {
            val exception = "[Exception in DetectorViewModel:getCustomers] [${e.localizedMessage}]".trimIndent()
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }

    fun uploadReading(consumerCode: String, jsId: Int, reading: Int, remarks: String, lat: Double, long: Double) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.uploadReading(consumerCode, jsId, reading, remarks, lat, long)))
        } catch (e: Exception) {
            val exception = "[Exception in DetectorViewModel:uploadReading] [${e.localizedMessage}]".trimIndent()
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }

    fun getDDR() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDDR()))
        } catch (e: Exception) {
            val exception = "[Exception in DetectorViewModel:getDDR] [${e.localizedMessage}]".trimIndent()
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }

    fun retrieveJobs() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.retrieveJobs()))
        } catch (e: Exception) {
            val exception = "[Exception in DetectorViewModel:retrieveJobs [${e.localizedMessage}]".trimIndent()
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }

    override fun onCleared() {
        try {
            super.onCleared()

            Log.i("DetectorViewModel", "DetectorViewModel destroyed!")
        } catch (e: Exception) {
            val exception = "[Exception in DetectorViewModel:onCleared] [${e.localizedMessage}]".trimIndent()
        }
    }
}