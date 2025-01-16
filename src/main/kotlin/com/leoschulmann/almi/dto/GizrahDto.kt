package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Gizrah
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GizrahDto(val id: Long, @SerialName("g") val value: String, @SerialName("ver") val version: Int)

fun Gizrah.toDto(): GizrahDto {
    return GizrahDto(id.value, value, version)
}
