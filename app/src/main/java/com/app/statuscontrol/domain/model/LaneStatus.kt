package com.app.statuscontrol.domain.model

data class LaneStatus(
    var id: String = "",
    val lane: String = "",
    var status: Boolean = false,
    var modifiedBy: String = "",
    var lastModification: String = ""
)
