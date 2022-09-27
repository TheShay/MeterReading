package com.itcurves.checkinapp.data.models.requests


import com.google.gson.annotations.SerializedName

data class StatusUpdateRequest(
    @SerializedName("AppVersion")
    val appVersion: String, // 1.0.0
    @SerializedName("CurrentLat")
    val currentLat: String, // 0.0
    @SerializedName("CurrentLong")
    val currentLong: String, // 0.0
    @SerializedName("CurrentLoc")
    val currentLoc: String, // 8201 Snoufer School Road, Gaithersburg, MD
    @SerializedName("DeviceNum")
    val deviceNum: String, // 353121074595239
    @SerializedName("Status")
    val status: String // ONLINE
)