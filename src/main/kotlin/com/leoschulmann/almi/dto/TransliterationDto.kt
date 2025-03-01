package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.VerbFormTransliteration
import com.leoschulmann.almi.enums.Lang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransliterationDto(
    val id: Long,
    @SerialName("v")
    val value: String,
    @SerialName("ver")
    val version: Int,
    val lang: Lang
)

fun VerbFormTransliteration.toDto() = TransliterationDto(id.value, value, version, lang)
