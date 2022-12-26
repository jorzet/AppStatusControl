package com.app.statuscontrol.domain.model

data class LaneStatus(
    val lane: String = "",
    val status: Boolean = false,
    val modifiedBy: String = "",
    val lastModification: String = ""
)
