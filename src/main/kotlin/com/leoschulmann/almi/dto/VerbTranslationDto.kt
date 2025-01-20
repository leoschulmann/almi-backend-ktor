package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.VerbTranslation
import com.leoschulmann.almi.enums.Lang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerbTranslationDto(
    val id: Long,
    @SerialName("t") val value: String,
    @SerialName("ver") val version: Int,
    @SerialName("l") val lang: Lang
)

fun VerbTranslation.toDto() = VerbTranslationDto(id.value, value, version, lang)
