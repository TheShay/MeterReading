package com.wasa.meterreading.data.api

import com.wasa.meterreading.utils.Utils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private var retrofitInstance: Retrofit? = null

    private fun getClient(baseURL: String): Retrofit? {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", "Bearer ${Utils.AUTH_TOKEN}").build()
                chain.proceed(request)
            }
            .build()

        if (retrofitInstance == null || !retrofitInstance?.baseUrl()?.equals(baseURL)!!) {
            retrofitInstance = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return retrofitInstance
    }

    val apiService: ApiService = getClient(Utils.BASE_URL)?.create(ApiService::class.java)!!
}