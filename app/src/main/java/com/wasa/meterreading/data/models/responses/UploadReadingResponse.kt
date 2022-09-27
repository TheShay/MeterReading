package com.wasa.meterreading.data.models.responses


import com.google.gson.annotations.SerializedName

data class UploadReadingResponse(
    @SerializedName("Result")
    val result: String,
    @SerializedName("success")
    val success: Boolean
)