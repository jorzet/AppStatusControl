package com.app.statuscontrol.domain.model

import java.io.Serializable

data class Notification(
    var id: String = "0",
    val lane: String = "",
    val status: Boolean = false,
    val lastModification: String = ""
): Serializable