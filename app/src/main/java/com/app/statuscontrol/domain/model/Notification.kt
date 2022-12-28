package com.app.statuscontrol.domain.model

data class Notification(
    var id: String = "1",
    val lane: String = "",
    val status: Boolean = false,
    val lastModification: String = ""
)