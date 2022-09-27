package com.wasa.meterreading.data.repository

import com.wasa.meterreading.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun getLogin(userName: String, password: String) = apiHelper.login(userName, password)
    suspend fun getLogout() = apiHelper.logout()
    suspend fun getCustomers(consumerCode: String, ddrId: Int) = apiHelper.receiveConsumer(consumerCode, ddrId)
    suspend fun uploadReading(consumerCode: String, jsId: Int, reading: Int, remarks: String, lat: Double, long: Double) = apiHelper.uploadReading(consumerCode, jsId, reading, remarks, lat, long)
    suspend fun getDDR() = apiHelper.getDDR()
    suspend fun retrieveJobs() = apiHelper.retrieveJobs()
}
