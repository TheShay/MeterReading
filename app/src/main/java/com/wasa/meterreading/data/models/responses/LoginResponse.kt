package com.wasa.meterreading.data.models.responses


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("Result")
    val result: Result
) {
    data class Result(
        @SerializedName("token")
        val token: String,
        @SerializedName("user")
        val user: User
    ) {
        data class User(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("email_verified_at")
            val emailVerifiedAt: Any,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}