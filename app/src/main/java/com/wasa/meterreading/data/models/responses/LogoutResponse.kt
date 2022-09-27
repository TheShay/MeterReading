package com.wasa.meterreading.data.models.responses


import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("msg")
    val msg: String,
    @SerializedName("success")
    val success: Boolean
)