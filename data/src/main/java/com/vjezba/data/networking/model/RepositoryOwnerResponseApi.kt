package com.vjezba.data.networking.model

import com.google.gson.annotations.SerializedName
import java.util.*


data class RepositoryOwnerResponseApi(
    @SerializedName("login")
    val login: String = "",
    @SerializedName("avatar_url")
    val avatarUrl: String = ""
)