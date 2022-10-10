package com.wasa.meterreading.data.models.responses


import com.google.gson.annotations.SerializedName

data class ReceiveConsumerResponse(
    @SerializedName("CustomerDetails")
    val customerDetails: CustomerDetails,
    @SerializedName("success")
    val success: Boolean
) {
    data class CustomerDetails(
        @SerializedName("Address")
        val address: String,
        @SerializedName("AndroidCode")
        val androidCode: Int,
        @SerializedName("Meter#")
        val meter: String,
        @SerializedName("Name")
        val name: String
    )
}