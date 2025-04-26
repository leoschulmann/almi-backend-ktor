package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Verb
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShortVerbDto(
    val id: Long,
    @SerialName("v") val value: String,
    @SerialName("ver") val version: Int,
    @SerialName("t") val translations: Map<String, String>
)

fun Verb.toDtoShort() = ShortVerbDto(
    id.value,
    value,
    version,
    translations.associate { it.lang.toString() to it.value }
)
