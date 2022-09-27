package com.wasa.meterreading.data.models.responses


import com.google.gson.annotations.SerializedName

class RetrieveJobsResponse : ArrayList<RetrieveJobsResponse.RetrieveJobsResponse1Item>(){
    data class RetrieveJobsResponse1Item(
        @SerializedName("id")
        val id: Int,
        @SerializedName("JS_Detail")
        val jSDetail: String
    )
}