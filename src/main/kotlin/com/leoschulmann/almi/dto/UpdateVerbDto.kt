package com.leoschulmann.almi.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateVerbDto(
    val id: Long,

    @SerialName("ver")
    val version: Int,

    @SerialName("v")
    val value: String,
)
