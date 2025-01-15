package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Gizrah

data class GizrahDto(val id: Long, val value: String, val version: Int)

fun Gizrah.toDto(): GizrahDto {
    return GizrahDto(id.value, value, version)
}
