package com.wasa.meterreading.data.api

import com.wasa.meterreading.data.models.responses.*
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("v2/consrec")
    suspend fun receiveConsumer(
        @Field("consumerCode") consumerCode: String,
        @Field("ddr_id") ddrID: Int
    ): ReceiveConsumerResponse

    @FormUrlEncoded
    @POST("v2/upJob")
    suspend fun uploadReading(
        @Field("consumerCode") consumerCode: String,
        @Field("js_id") jsId: Int,
        @Field("reading") reading: Int,
        @Field("Remarks") Remarks: String,
        @Field("image") image: String,
        @Field("Lat") Lat: Double,
        @Field("Lng") Lng: Double,
    ): UploadReadingResponse

    @FormUrlEncoded
    @POST("v2/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): LoginResponse

    @POST("v2/logout")
    suspend fun logout(): LogoutResponse

    @GET("v2/ddr")
    suspend fun getDDR(): DDRResponse

    @GET("v2/retjobs")
    suspend fun retrieveJob(): RetrieveJobsResponse
}