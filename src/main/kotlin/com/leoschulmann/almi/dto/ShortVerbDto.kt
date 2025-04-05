package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Verb
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShortVerbDto(
    val id: Long,
    @SerialName("v") val value: String,
    @SerialName("ver") val version: Int
)

fun Verb.toDtoShort() = ShortVerbDto(
    id.value,
    value,
    version,
)
