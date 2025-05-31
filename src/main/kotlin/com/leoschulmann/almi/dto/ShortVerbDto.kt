package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Verb
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShortVerbDto(
    val id: Long,
    @SerialName("v") val value: String,
    @SerialName("ver") val version: Int,
    @SerialName("t") val translations: List<VerbTranslationDto>
)

fun Verb.toDtoShort() = ShortVerbDto(
    id.value,
    value,
    version,
    translations.map { it.toDto() }
)
