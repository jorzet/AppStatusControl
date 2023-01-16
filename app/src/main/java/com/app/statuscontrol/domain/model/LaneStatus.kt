package com.app.statuscontrol.domain.model

data class LaneStatus(
    var id: String = "",
    var lane: String = "",
    var status: Boolean = false,
    var modifiedBy: String = "",
    var lastModification: String = "",
    var userUid: String = ""
)
