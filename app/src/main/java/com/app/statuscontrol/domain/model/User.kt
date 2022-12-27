package com.app.statuscontrol.domain.model

data class User(
    val uid: String = "",
    val email: String = "",
    val password: String = "",
    val nick: String = "",
    val name: String = "",
    val lane: String = "Caja 1",
    val laneId: String = "1",
    var status: Boolean = false
)
