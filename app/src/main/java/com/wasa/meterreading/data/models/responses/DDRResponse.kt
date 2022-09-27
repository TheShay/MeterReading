package com.wasa.meterreading.data.models.responses


import com.google.gson.annotations.SerializedName

data class DDRResponse(
    @SerializedName("DDR")
    val dDR: List<DDR>,
    @SerializedName("success")
    val success: Boolean
) {
    data class DDR(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("DDR_Abb")
        val dDRAbb: String,
        @SerializedName("DDR_Desc")
        val dDRDesc: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}