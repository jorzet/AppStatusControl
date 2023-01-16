package com.app.statuscontrol.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    val uid: String = "",
    val email: String = "",
    val password: String = "",
    val nick: String = "",
    val name: String = "",
    var lane: String = "Caja 1",
    var laneId: String = "1",
    var status: Boolean = false,
    var userType: String = "",
    var deviceID: String = ""
)
