package com.leoschulmann.almi.dto

import com.leoschulmann.almi.enums.Lang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateVerbTranslationDto(@SerialName("t") val value: String, @SerialName("l") val lang: Lang)
