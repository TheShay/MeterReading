package com.wasa.meterreading.data.api

class ApiHelper(private val apiService: ApiService) {
    suspend fun login(userName: String, password: String) = apiService.login(userName, password)
    suspend fun logout() = apiService.logout()
    suspend fun receiveConsumer(consumerCode: String, ddrId: Int) = apiService.receiveConsumer(consumerCode, ddrId)
    suspend fun uploadReading(consumerCode: String, jsId: Int, reading: Int, remarks: String, image: String, lat: Double, long: Double) = apiService.uploadReading(consumerCode, jsId, reading, remarks, image, lat, long)
    suspend fun getDDR() = apiService.getDDR()
    suspend fun retrieveJobs() = apiService.retrieveJob()
}