package com.app.statuscontrol.domain.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

enum class UserType( val userType: String) {
    @Expose
    @SerializedName("admin")
    ADMIN("admin"),


    @Expose
    @SerializedName("employee")
    EMPLOYEE("employee"),

    @Expose
    @SerializedName("consumer")
    CONSUMER("consumer"),

    NONE("")
}